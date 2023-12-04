package com.ydanneg.erply.data.repository

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.crypto.EncryptionManager
import com.ydanneg.erply.data.api.ErplyApiDataSource
import com.ydanneg.erply.data.datastore.UserSessionDataSource
import com.ydanneg.erply.data.datastore.mapper.toModel
import com.ydanneg.erply.datastore.passwordOrNull
import com.ydanneg.erply.model.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private const val ENCRYPTION_KEY_ALIAS = "userPasswordKey"

@OptIn(ExperimentalCoroutinesApi::class)
class UserSessionRepository @Inject constructor(
    private val erplyApiDataSource: ErplyApiDataSource,
    private val userSessionDataSource: UserSessionDataSource,
    private val encryptionManager: EncryptionManager
) {
    val userSession: Flow<UserSession> = userSessionDataSource.userSession.map { userSessionProto ->
        val encryptedPassword = userSessionProto.passwordOrNull?.let {
            encryptionManager.decryptText(
                keyAlias = ENCRYPTION_KEY_ALIAS,
                encryptedData = it.value.toByteArray(),
                iv = it.iv.toByteArray()
            )
        }
        userSessionProto.toModel(encryptedPassword)
    }

    suspend fun login(clientCode: String, username: String, password: String) {
        val verifiedUser = erplyApiDataSource.login(clientCode, username, password)
        val encryptedPassword = encryptionManager.encryptText(ENCRYPTION_KEY_ALIAS, password)
        userSessionDataSource.setVerifiedUser(clientCode, encryptedPassword, verifiedUser)
    }

    private suspend fun reLogin() {
        val storedSession = userSession.first()
        login(storedSession.clientCode, storedSession.username, storedSession.password!!)
    }

    suspend fun <T> tryLoginIf401(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: ErplyApiException) {
            if (e.type == ErplyApiError.SessionExpired) {
                reLogin()
                block()
            } else {
                throw e
            }
        }
    }

    suspend fun logout() {
        userSessionDataSource.clear()
    }

    fun <T> withClientCode(block: (String) -> Flow<T>): Flow<T> =
        userSession
            .map { it.clientCode }
            .distinctUntilChanged()
            .flatMapLatest {
                block(it)
            }
}
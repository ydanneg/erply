package com.ydanneg.erply.data.repository

import com.ydanneg.erply.data.api.ErplyApiDataSource
import com.ydanneg.erply.data.datastore.UserSessionDataSource
import com.ydanneg.erply.data.datastore.mapper.toModel
import com.ydanneg.erply.datastore.passwordOrNull
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.crypto.EncryptionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
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
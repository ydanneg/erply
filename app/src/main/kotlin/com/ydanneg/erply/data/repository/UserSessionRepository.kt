package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.crypto.EncryptionManager
import com.ydanneg.erply.data.api.ErplyNetworkDataSource
import com.ydanneg.erply.data.datastore.UserSessionDataSource
import com.ydanneg.erply.data.datastore.mapper.toModel
import com.ydanneg.erply.datastore.passwordOrNull
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject


private const val ENCRYPTION_KEY_ALIAS = "userPasswordKey"

@OptIn(ExperimentalCoroutinesApi::class)
class UserSessionRepository @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
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
        val verifiedUser = erplyNetworkDataSource.login(clientCode, username, password)
        val encryptedPassword = encryptionManager.encryptText(ENCRYPTION_KEY_ALIAS, password)
        userSessionDataSource.setVerifiedUser(clientCode, encryptedPassword, verifiedUser)
    }

    suspend fun tryLogin() {
        with(userSession.first()) { login(clientCode, username, password!!) }
    }

    suspend fun <T> tryAuthenticateUnauthorized(enabled: Boolean = true, block: suspend (UserSession) -> T): T {
        return try {
            val first = userSession.first()
            Log.i(TAG, "tryAuthenticateUnauthorized.executing: $first")
            block(first)
        } catch (e: ErplyApiException) {
            Log.e(TAG, "tryAuthenticateUnauthorized: $e")
            if (enabled && e.type == ErplyApiError.Unauthorized) {
            Log.i(TAG, "tryAuthenticateUnauthorized.reLogin()...")
                tryLogin()
                val first = userSession.first()
                Log.i(TAG, "tryAuthenticateUnauthorized.block(): $first")
                block(first)
            } else {
                throw e
            }
        }
    }

    suspend fun logout() {
        userSessionDataSource.clear()
    }

    fun <T> withClientCode(block: suspend (String) -> Flow<T>): Flow<T> =
        userSession
            .map { it.clientCode }
            .distinctUntilChanged()
            .flatMapLatest {
                block(it)
            }

    fun <T> withToken(block: suspend (String) -> Flow<T>): Flow<T> =
        userSession
            .mapNotNull { it.token }
            .distinctUntilChanged()
            .flatMapLatest {
                block(it)
            }
}

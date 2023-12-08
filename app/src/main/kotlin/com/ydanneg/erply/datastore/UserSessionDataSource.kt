package com.ydanneg.erply.datastore

import androidx.datastore.core.DataStore
import com.ydanneg.erply.datastore.mapper.toModel
import com.ydanneg.erply.datastore.mapper.toProto
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.security.EncryptionManager
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val ENCRYPTION_KEY_ALIAS = "userPasswordKey"

class UserSessionDataSource @Inject constructor(
    private val userSessionDataStore: DataStore<UserSessionProto>,
    private val encryptionManager: EncryptionManager
) {

    val userSession = userSessionDataStore.data.map {
        val password = it.passwordOrNull?.let { encryptedData ->
            encryptionManager.decryptText(
                keyAlias = ENCRYPTION_KEY_ALIAS,
                encrypted = encryptedData.value.toByteArray(),
                iv = encryptedData.iv.toByteArray()
            )
        }
        it.toModel(password)
    }

    suspend fun updateUserSession(userSession: UserSession) {
        runCatching {
            val encryptedPasswordData = userSession.password?.let { encryptionManager.encryptText(ENCRYPTION_KEY_ALIAS, it) }
            userSessionDataStore.updateData {
                userSession.toProto(it, encryptedPasswordData)
            }
        }
    }

    suspend fun clear() {
        runCatching {
            userSessionDataStore.updateData {
                it.copy {
                    clearUserId()
                    clearUsername()
                    clearToken()
                    clearClientCode()
                    clearPassword()
                }
            }
        }
    }
}

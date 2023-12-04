package com.ydanneg.erply.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.google.protobuf.kotlin.toByteString
import com.ydanneg.erply.api.model.ErplyVerifiedUser
import com.ydanneg.erply.datastore.UserSessionProto
import com.ydanneg.erply.datastore.copy
import com.ydanneg.erply.crypto.EncryptedData
import com.ydanneg.erply.util.LogUtils.TAG
import java.io.IOException
import javax.inject.Inject

class UserSessionDataSource @Inject constructor(
    private val userSessionDataStore: DataStore<UserSessionProto>
) {

    val userSession = userSessionDataStore.data

    suspend fun setVerifiedUser(clientCode: String, encryptedPassword: EncryptedData, verifiedUser: ErplyVerifiedUser) {
        try {
            userSessionDataStore.updateData {
                it.copy {
                    userId = verifiedUser.userId
                    username = verifiedUser.username
                    token = verifiedUser.token
                    this.clientCode = clientCode
                    password = UserSessionProto.EncryptedPasswordProto.getDefaultInstance().copy {
                        value = encryptedPassword.data.toByteString()
                        iv = encryptedPassword.iv.toByteString()
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to update user session", e)
        }
    }

    suspend fun clear() {
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
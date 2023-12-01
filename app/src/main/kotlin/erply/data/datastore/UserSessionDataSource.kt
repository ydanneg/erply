package erply.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.ydanneg.erply.datastore.UserSession
import com.ydanneg.erply.datastore.copy
import com.ydanneg.erply.model.ErplyVerifiedUser
import erply.util.LogUtils.TAG
import java.io.IOException
import javax.inject.Inject

class UserSessionDataSource @Inject constructor(
    private val userSession: DataStore<UserSession>
) {

    val userSessionData = userSession.data

    suspend fun setVerifiedUser(verifiedUser: ErplyVerifiedUser) {
        try {
            userSession.updateData {
                it.copy {
                    userId = verifiedUser.userId
                    username = verifiedUser.username
                    token = verifiedUser.token
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to update user session", e)
        }
    }

    suspend fun clear() {
        userSession.updateData {
            it.copy {
                userId = ""
                username = ""
                token = ""
            }
        }
    }
}
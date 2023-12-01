package erply.data.repository

import com.ydanneg.erply.api.ErplyApi
import com.ydanneg.erply.datastore.UserSession
import erply.data.datastore.UserSessionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSessionRepository @Inject constructor(
    private val erplyApi: ErplyApi,
    private val userSessionDataSource: UserSessionDataSource
) {
    val userSessionData: Flow<UserSession> = userSessionDataSource.userSessionData
    val isLoggedIn = userSessionDataSource.userSessionData.map { it.token.isNotBlank() }

    suspend fun login(clientCode: String, username: String, password: String) {
        val verifiedUser = erplyApi.auth.login(clientCode, username, password)
        userSessionDataSource.setVerifiedUser(verifiedUser)
    }


    suspend fun logout() {
        userSessionDataSource.clear()
    }
}
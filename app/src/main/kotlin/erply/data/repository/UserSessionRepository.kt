package erply.data.repository

import com.ydanneg.erply.datastore.UserSession
import erply.data.api.ErplyApiDataSource
import erply.data.datastore.UserSessionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSessionRepository @Inject constructor(
    private val erplyApiDataSource: ErplyApiDataSource,
    private val userSessionDataSource: UserSessionDataSource
) {
    val userSessionData: Flow<UserSession> = userSessionDataSource.userSessionData
    val isLoggedIn = userSessionDataSource.userSessionData.map { it.token.isNotBlank() }

    suspend fun login(clientCode: String, username: String, password: String) {
        val verifiedUser = erplyApiDataSource.login(clientCode, username, password)
        userSessionDataSource.setVerifiedUser(verifiedUser)
    }


    suspend fun logout() {
        userSessionDataSource.clear()
    }
}
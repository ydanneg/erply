package erply.data.repository

import erply.data.api.ErplyApiDataSource
import erply.data.datastore.UserSessionDataSource
import erply.data.datastore.mapper.toModel
import erply.model.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class UserSessionRepository @Inject constructor(
    private val erplyApiDataSource: ErplyApiDataSource,
    private val userSessionDataSource: UserSessionDataSource
) {
    val userSession: Flow<UserSession> = userSessionDataSource.userSession.map { it.toModel() }

    suspend fun login(clientCode: String, username: String, password: String) {
        val verifiedUser = erplyApiDataSource.login(clientCode, username, password)
        userSessionDataSource.setVerifiedUser(clientCode, verifiedUser)
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
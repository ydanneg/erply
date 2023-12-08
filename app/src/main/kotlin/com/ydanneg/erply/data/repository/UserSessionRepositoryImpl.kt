package com.ydanneg.erply.data.repository

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.network.api.toModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
class UserSessionRepositoryImpl @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
    private val userSessionDataSource: UserSessionDataSource
) : UserSessionRepository {
    override val userSession: Flow<UserSession> = userSessionDataSource.userSession

    override suspend fun login(clientCode: String, username: String, password: String) {
        val userSession = erplyNetworkDataSource.login(clientCode, username, password).toModel(clientCode, password)
        userSessionDataSource.updateUserSession(userSession)
    }

    override suspend fun tryLogin() {
        with(userSession.first()) { login(clientCode, username, password!!) }
    }

    override suspend fun <T> tryAuthenticateUnauthorized(enabled: Boolean, block: suspend (UserSession) -> T): T {
        return try {
            block(userSession.first())
        } catch (e: ErplyApiException) {
//            Log.e(TAG, "API Error", e)//NON-NLS
            if (enabled && e.type == ErplyApiError.Unauthorized) {
//                Log.i(TAG, "trying to re-authenticate...")//NON-NLS
                tryLogin()
//                Log.i(TAG, "re-trying operation...")//NON-NLS
                block(userSession.first())
            } else {
                throw e
            }
        }
    }

    override suspend fun logout() {
        // TODO: should we clear DB? Probably not, no sensitive info there and next login will use fast sync.
        userSessionDataSource.clear()
    }

    override fun <T> withClientCode(block: suspend (String) -> Flow<T>): Flow<T> =
        userSession
            .map { it.clientCode }
            .distinctUntilChanged()
            .flatMapLatest {
                block(it)
            }
}

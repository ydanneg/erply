package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.network.api.toModel
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
class UserSessionRepository @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
    private val userSessionDataSource: UserSessionDataSource
) {
    val userSession: Flow<UserSession> = userSessionDataSource.userSession

    suspend fun login(clientCode: String, username: String, password: String) {
        val userSession = erplyNetworkDataSource.login(clientCode, username, password).toModel(clientCode, password)
        userSessionDataSource.updateUserSession(userSession)
    }

    suspend fun tryLogin() {
        with(userSession.first()) { login(clientCode, username, password!!) }
    }

    suspend fun <T> tryAuthenticateUnauthorized(enabled: Boolean = true, block: suspend (UserSession) -> T): T {
        return try {
            block(userSession.first())
        } catch (e: ErplyApiException) {
            Log.e(TAG, "API Error", e)//NON-NLS
            if (enabled && e.type == ErplyApiError.Unauthorized) {
                Log.i(TAG, "trying to re-authenticate...")//NON-NLS
                tryLogin()
                Log.i(TAG, "re-trying operation...")//NON-NLS
                block(userSession.first())
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
}

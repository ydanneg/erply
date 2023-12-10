package com.ydanneg.erply.data.repository

import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.network.api.toModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserSessionRepositoryImpl @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
    private val userSessionDataSource: UserSessionDataSource
) : UserSessionRepository {
    override val userSession: Flow<UserSession> = userSessionDataSource.userSession

    override suspend fun login(clientCode: String, username: String, password: String) {
        val userSession = erplyNetworkDataSource.login(clientCode, username, password).toModel(clientCode, password)
        userSessionDataSource.updateUserSession(userSession)
    }

    override suspend fun logout() {
        // TODO: should we clear DB? Probably not, no sensitive info there and next login will use fast sync.
        userSessionDataSource.clear()
    }
}

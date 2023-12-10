package com.ydanneg.erply.test.doubles

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.model.UserSession
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

object FakeUserSessionRepository : UserSessionRepository {

    private val _userSession = MutableSharedFlow<UserSession>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val userSession: Flow<UserSession> = _userSession.filterNotNull()

    override suspend fun login(clientCode: String, username: String, password: String) {
        delay(100)
        _userSession.tryEmit(testUserSession)
    }

    override suspend fun logout() {
        _userSession.tryEmit(testEmptyUserSession)
    }

    private val testUserSession = UserSession(
        clientCode = "clientCode",
        userId = "userId",
        username = "username",
        token = "token",
        password = "password"
    )
    private val testEmptyUserSession = UserSession(
        clientCode = "",
        userId = "",
        username = "",
        token = null,
        password = null
    )
}

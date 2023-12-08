package com.ydanneg.erply.data.repository

import com.ydanneg.erply.model.UserSession
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    val userSession: Flow<UserSession>

    suspend fun login(clientCode: String, username: String, password: String)

    suspend fun tryLogin()

    suspend fun <T> tryAuthenticateUnauthorized(enabled: Boolean = true, block: suspend (UserSession) -> T): T

    suspend fun logout()
    fun <T> withClientCode(block: suspend (String) -> Flow<T>): Flow<T>
}

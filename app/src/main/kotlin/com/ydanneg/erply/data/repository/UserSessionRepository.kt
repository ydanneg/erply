@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ydanneg.erply.data.repository

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.model.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

interface UserSessionRepository {
    val userSession: Flow<UserSession>

    suspend fun login(clientCode: String, username: String, password: String)

    suspend fun logout()
    suspend fun <T> tryAuthenticateUnauthorized(enabled: Boolean = true, block: suspend (UserSession) -> T): T {
        return try {
            block(userSession.first())
        } catch (e: ErplyApiException) {
            if (enabled && e.type == ErplyApiError.Unauthorized) {
                tryLogin()
                block(userSession.first())
            } else {
                throw e
            }
        }
    }

    suspend fun tryLogin() = with(userSession.first()) { login(clientCode, username, password!!) }

    fun <T> withClientCode(block: suspend (String) -> Flow<T>): Flow<T> =
        userSession
            .map { it.clientCode }
            .distinctUntilChanged()
            .flatMapLatest {
                block(it)
            }
}

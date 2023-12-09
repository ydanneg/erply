package com.ydanneg.erply.domain

import android.util.Log
import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory

abstract class AbstractAuthenticationAwareUseCase(
    userPreferencesDataSource: UserPreferencesDataSource,
    private val userSessionRepository: UserSessionRepository,
) {
    val log = LoggerFactory.getLogger("AbstractAuthenticationAwareUseCase")

    private val isKeepMeSignedIn = userPreferencesDataSource.userPreferences
        .map { it.isKeepMeSignedIn }

    protected suspend fun <T> withAuthenticationAware(defaultValue: T, block: suspend (UserSession) -> T): T {
        val keepMeSignedIn = isKeepMeSignedIn.first()
        try {
            return userSessionRepository.tryAuthenticateUnauthorized(keepMeSignedIn, block)
        } catch (e: ErplyApiException) {
            log.error("withAuthenticationAware", e)
            if (e.type == ErplyApiError.Unauthorized) {
                // still 401? log out now!
                userSessionRepository.logout()
                return defaultValue
            }
            throw e
        }
    }

}

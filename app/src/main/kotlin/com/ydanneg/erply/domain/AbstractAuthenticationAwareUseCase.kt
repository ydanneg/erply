package com.ydanneg.erply.domain

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.model.UserSession
import kotlinx.coroutines.flow.first
import org.slf4j.LoggerFactory

abstract class AbstractAuthenticationAwareUseCase(
    private val userSessionRepository: UserSessionRepository,
) {
    private val log = LoggerFactory.getLogger("AbstractAuthenticationAwareUseCase")

    protected suspend fun <T> ensureAuthenticated(defaultValue: T, block: suspend (UserSession) -> T): T {
        val keepMeSignedIn = userSessionRepository.userSession.first().password != null
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

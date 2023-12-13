package com.ydanneg.erply.domain

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import javax.inject.Inject

class GetServerVersionUseCase @Inject constructor(
    val userSessionRepository: UserSessionRepository,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userSessionRepository) {

    suspend operator fun invoke(): Long {
        return ensureAuthenticated(0) { auth ->
            erplyNetworkDataSource.fetchServerTimestamp(auth.token!!)
        }
    }

}

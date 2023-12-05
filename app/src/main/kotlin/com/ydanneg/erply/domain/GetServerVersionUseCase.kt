package com.ydanneg.erply.domain

import com.ydanneg.erply.data.api.ErplyNetworkDataSource
import com.ydanneg.erply.data.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.UserSessionRepository
import javax.inject.Inject

class GetServerVersionUseCase @Inject constructor(
    val userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userPreferencesDataSource, userSessionRepository) {

    suspend operator fun invoke(): Long {
        return withAuthenticationAware(0) { auth ->
            erplyNetworkDataSource.fetchServerTimestamp(auth.token!!)
        }
    }

}

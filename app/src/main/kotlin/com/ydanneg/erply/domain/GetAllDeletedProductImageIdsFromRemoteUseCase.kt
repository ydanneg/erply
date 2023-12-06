package com.ydanneg.erply.domain

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllDeletedProductImageIdsFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userPreferencesDataSource, userSessionRepository) {

    suspend operator fun invoke(changedSince: Long? = null): Flow<List<String>> =
        withAuthenticationAware(flowOf()) { auth ->
            erplyNetworkDataSource.fetchDeletedImageIds(auth.token!!, changedSince)
                .map { image -> image.map { it.id } }
        }
}

package com.ydanneg.erply.domain

import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAllDeletedProductsFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userPreferencesDataSource, userSessionRepository) {

    suspend operator fun invoke(changedSince: Long): Flow<List<String>> {
        return withAuthenticationAware(flowOf()) { auth ->
            erplyNetworkDataSource.fetchAllDeletedProductIds(auth.token!!, changedSince)
        }
    }
}

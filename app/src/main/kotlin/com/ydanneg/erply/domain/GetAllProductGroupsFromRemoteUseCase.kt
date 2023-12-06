package com.ydanneg.erply.domain

import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAllProductGroupsFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userPreferencesDataSource, userSessionRepository) {

    suspend operator fun invoke(changedSince: Long?): Flow<List<ErplyProductGroup>> =
        withAuthenticationAware(flowOf()) { auth ->
            erplyNetworkDataSource.fetchAllProductGroups(auth.token!!, changedSince)
        }
}

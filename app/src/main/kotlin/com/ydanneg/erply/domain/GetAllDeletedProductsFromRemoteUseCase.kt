package com.ydanneg.erply.domain

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAllDeletedProductsFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userSessionRepository) {

    suspend operator fun invoke(changedSince: Long): Flow<List<String>> {
        return ensureAuthenticated(flowOf()) { auth ->
            erplyNetworkDataSource.fetchAllDeletedProductIds(auth.token!!, changedSince)
        }
    }
}

package com.ydanneg.erply.domain

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllDeletedProductImageIdsFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userSessionRepository) {

    suspend operator fun invoke(changedSince: Long? = null): Flow<List<String>> =
        ensureAuthenticated(flowOf()) { auth ->
            erplyNetworkDataSource.fetchDeletedImageIds(auth.token!!, changedSince)
                .map { image -> image.map { it.id } }
        }
}

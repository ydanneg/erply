package com.ydanneg.erply.domain

import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAllProductsFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userPreferencesDataSource, userSessionRepository) {

    suspend operator fun invoke(changedSince: Long? = null): Flow<List<ErplyProduct>> =
        withAuthenticationAware(flowOf()) { auth ->
            erplyNetworkDataSource.fetchAllProducts(auth.token!!, changedSince)
        }
}

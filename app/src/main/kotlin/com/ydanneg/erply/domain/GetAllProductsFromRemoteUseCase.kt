package com.ydanneg.erply.domain

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.model.Product
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.network.api.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllProductsFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userSessionRepository) {

    suspend operator fun invoke(changedSince: Long? = null): Flow<List<Product>> =
        ensureAuthenticated(flowOf()) { auth ->
            erplyNetworkDataSource.fetchAllProducts(auth.token!!, changedSince).map { it.map { product -> product.toModel() } }
        }
}

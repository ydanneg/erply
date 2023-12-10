package com.ydanneg.erply.domain

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.model.ProductImage
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.network.api.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllProductImagesFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userPreferencesDataSource, userSessionRepository) {

    suspend operator fun invoke(changedSince: Long? = null): Flow<List<ProductImage>> =
        ensureAuthenticated(flowOf()) { auth ->
            erplyNetworkDataSource.fetchAllImages(auth.token!!, changedSince).map { it.map { image -> image.toModel() } }
        }
}

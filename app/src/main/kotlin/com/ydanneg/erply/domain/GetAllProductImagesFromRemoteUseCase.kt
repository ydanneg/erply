package com.ydanneg.erply.domain

import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.data.api.ErplyNetworkDataSource
import com.ydanneg.erply.data.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAllProductImagesFromRemoteUseCase @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    private val erplyNetworkDataSource: ErplyNetworkDataSource
) : AbstractAuthenticationAwareUseCase(userPreferencesDataSource, userSessionRepository) {

    suspend operator fun invoke(token: String, changedSince: Long? = null): Flow<List<ErplyProductPicture>> =
        withAuthenticationAware(flowOf()) {
            erplyNetworkDataSource.fetchAllImages(token, changedSince)
        }
}

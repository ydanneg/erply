package com.ydanneg.erply.domain

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.data.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.ProductsRepository
import com.ydanneg.erply.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductsFromRemoteUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource
) {

    private val isKeepMeSignedIn = userPreferencesDataSource.userPreferences
        .map { it.isKeepMeSignedIn }

    suspend operator fun invoke(groupId: String): List<ErplyProduct> {
        val keepMeSignedIn = isKeepMeSignedIn.first()
        try {
            return userSessionRepository.tryAuthenticateUnauthorized(keepMeSignedIn) { productsRepository.updateProductsByGroupId(groupId) }
        } catch (e: ErplyApiException) {
            if (e.type == ErplyApiError.Unauthorized) {
                // still 401? log out now!
                userSessionRepository.logout()
                return emptyList()
            }
            throw e
        }
    }

}
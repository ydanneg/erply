package com.ydanneg.erply.domain

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

    private val keepMeSignedIn = userPreferencesDataSource.userPreferences
        .map { it.keepMeSignedIn }

    suspend operator fun invoke(groupId: String): List<ErplyProduct> {
        val keepMeSignedIn = keepMeSignedIn.first()
        if (keepMeSignedIn) {
            return userSessionRepository.tryLoginIf401 { productsRepository.updateProductsByGroupId(groupId) }
        }
        return productsRepository.updateProductsByGroupId(groupId)
    }

}
package com.ydanneg.erply.domain

import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.data.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductGroupsFromRemoteUseCase @Inject constructor(
    private val productGroupsRepository: ProductGroupsRepository,
    private val userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource
) {
    private val isKeepMeSignedIn = userPreferencesDataSource.userPreferences
        .map { it.isKeepMeSignedIn }

    suspend operator fun invoke(): List<ErplyProductGroup> {
        if (isKeepMeSignedIn.first()) {
            return userSessionRepository.tryLoginIf401 { productGroupsRepository.updateProductGroups() }
        }
        return productGroupsRepository.updateProductGroups()
    }
}
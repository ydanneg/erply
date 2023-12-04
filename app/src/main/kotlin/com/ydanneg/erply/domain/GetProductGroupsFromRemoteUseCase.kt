package com.ydanneg.erply.domain

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
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
        val keepMeSignedIn = isKeepMeSignedIn.first()
        try {
            return userSessionRepository.tryAuthenticateUnauthorized(keepMeSignedIn) { productGroupsRepository.updateProductGroups() }
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
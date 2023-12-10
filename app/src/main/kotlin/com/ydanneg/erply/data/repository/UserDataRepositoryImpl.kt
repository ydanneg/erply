package com.ydanneg.erply.data.repository

import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.model.UserData
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource
) : UserDataRepository {

    override val userData = combine(
        userSessionRepository.userSession,
        userPreferencesDataSource.userPreferences
    ) { userSession, userPreferences ->
        UserData(
            session = userSession,
            prefs = userPreferences
        )
    }
}

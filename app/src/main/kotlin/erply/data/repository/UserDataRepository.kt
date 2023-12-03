package erply.data.repository

import erply.data.datastore.UserPreferencesDataSource
import erply.model.UserData
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserDataRepository @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource
) {
    val userData = combine(userSessionRepository.userSession, userPreferencesDataSource.userPreferences) { userSession, userPreferences ->
        UserData(
            session = userSession,
            prefs = userPreferences
        )
    }
}
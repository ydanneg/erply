package com.ydanneg.erply.test.doubles

import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.model.UserPreferences
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

object FakeUserPreferencesDataSource : UserPreferencesDataSource {
    private val _userPreferences = MutableSharedFlow<UserPreferences>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentUserPreferences get() = _userPreferences.replayCache.firstOrNull() ?: UserPreferences()

    override val userPreferences: Flow<UserPreferences> = _userPreferences.filterNotNull()

    override suspend fun updateChangeListVersion(clientCode: String, update: LastSyncTimestamps.() -> LastSyncTimestamps) {
        val updated = update(currentUserPreferences.lastSyncTimestamps)
        _userPreferences.tryEmit(currentUserPreferences.copy(lastSyncTimestamps = updated))
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        _userPreferences.tryEmit(currentUserPreferences.copy(darkThemeConfig = darkThemeConfig))
    }

    override suspend fun setKeepMeSignedIn(value: Boolean) {
        _userPreferences.tryEmit(currentUserPreferences.copy(isKeepMeSignedIn = value))
    }

}

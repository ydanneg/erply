package com.ydanneg.erply.datastore

import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val userPreferences: Flow<UserPreferences>

    suspend fun updateChangeListVersion(clientCode: String, update: LastSyncTimestamps.() -> LastSyncTimestamps)

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
}

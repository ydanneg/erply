package com.ydanneg.erply.model


enum class DarkThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}

data class LastSyncTimestamps(
    val productGroupsLastSyncTimestamp: Long = 0,
    val productsLastSyncTimestamp: Long = 0,
    val picturesLastSyncTimestamp: Long = 0
)

data class UserPreferences(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val lastSyncTimestamps: LastSyncTimestamps = LastSyncTimestamps()
)

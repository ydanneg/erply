package com.ydanneg.erply.model


enum class DarkThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}

data class UserPreferences(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val isKeepMeSignedIn: Boolean = false,
    val lastSyncTimestamps: LastSyncTimestamps = LastSyncTimestamps()
)

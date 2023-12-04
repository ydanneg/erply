package com.ydanneg.erply.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.ydanneg.erply.datastore.DarkThemeConfigProto
import com.ydanneg.erply.datastore.UserPreferencesProto
import com.ydanneg.erply.datastore.copy
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>
) {

    val userPreferences = dataStore.data.map {
        UserPreferences(
            darkThemeConfig = when (it.darkThemeConfig) {
                DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                else -> DarkThemeConfig.FOLLOW_SYSTEM
            },
            isKeepMeSignedIn = it.keepMeSignedIn
        )
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        try {
            dataStore.updateData {
                it.copy {
                    this.darkThemeConfig = when (darkThemeConfig) {
                        DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                        DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                        DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to update user preferences", e)
        }
    }

    suspend fun setKeepMeSignedIn(value: Boolean) {
        try {
            dataStore.updateData {
                it.copy {
                    keepMeSignedIn = value
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to update user preferences", e)
        }
    }
}
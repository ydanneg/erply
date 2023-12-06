package com.ydanneg.erply.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.distinctUntilChanged
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
            isKeepMeSignedIn = it.keepMeSignedIn,
            lastSyncTimestamps = LastSyncTimestamps(
                productsLastSyncTimestamp = it.productsLastSyncTimestamp,
                productGroupsLastSyncTimestamp = it.productGroupsLastSyncTimestamp,
                picturesLastSyncTimestamp = it.imagesLastSyncTimestamp

            )
        )
    }.distinctUntilChanged()

    suspend fun updateChangeListVersion(update: LastSyncTimestamps.() -> LastSyncTimestamps) {
        dataStore.updateData {
            val updatedLastSyncTimestamps = update(
                LastSyncTimestamps(
                    productsLastSyncTimestamp = it.productsLastSyncTimestamp,
                    productGroupsLastSyncTimestamp = it.productGroupsLastSyncTimestamp,
                    picturesLastSyncTimestamp = it.imagesLastSyncTimestamp
                )
            )
            it.copy {
                productsLastSyncTimestamp = updatedLastSyncTimestamps.productsLastSyncTimestamp
                productGroupsLastSyncTimestamp = updatedLastSyncTimestamps.productGroupsLastSyncTimestamp
                imagesLastSyncTimestamp = updatedLastSyncTimestamps.picturesLastSyncTimestamp
            }
        }
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

package com.ydanneg.erply.datastore

import androidx.datastore.core.DataStore
import com.ydanneg.erply.datastore.mapper.toModel
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.LastSyncTimestamps
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>
) {

    val userPreferences = dataStore.data.map { it.toModel() }.distinctUntilChanged()

    suspend fun updateChangeListVersion(update: LastSyncTimestamps.() -> LastSyncTimestamps) {
        runCatching {
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
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        runCatching {
            dataStore.updateData {
                it.copy {
                    this.darkThemeConfig = when (darkThemeConfig) {
                        DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                        DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                        DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    }
                }
            }
        }
    }

    suspend fun setKeepMeSignedIn(value: Boolean) {
        runCatching {
            dataStore.updateData {
                it.copy {
                    keepMeSignedIn = value
                }
            }
        }
    }
}

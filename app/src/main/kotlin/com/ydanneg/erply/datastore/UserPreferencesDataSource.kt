package com.ydanneg.erply.datastore

import androidx.datastore.core.DataStore
import com.ydanneg.erply.datastore.mapper.toModel
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.LastSyncTimestamps
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>,
    userSessionDataSource: UserSessionDataSource
) {

    val userPreferences = userSessionDataSource.userSession.flatMapLatest { session ->
        dataStore.data.map { it.toModel(session.clientCode) }
            .distinctUntilChanged()
    }

    suspend fun updateChangeListVersion(clientCode: String, update: LastSyncTimestamps.() -> LastSyncTimestamps) {
        runCatching {
            dataStore.updateData {
                val updatedLastSyncTimestamps = update(
                    LastSyncTimestamps(
                        productsLastSyncTimestamp = it.getProductsLastSyncTimestampOrDefault(clientCode, 0),
                        productGroupsLastSyncTimestamp = it.getGroupsLastSyncTimestampOrDefault(clientCode, 0),
                        picturesLastSyncTimestamp = it.getImagesLastSyncTimestampOrDefault(clientCode, 0)
                    )
                )
                it.toBuilder()
                    .putProductsLastSyncTimestamp(clientCode, updatedLastSyncTimestamps.productsLastSyncTimestamp)
                    .putGroupsLastSyncTimestamp(clientCode, updatedLastSyncTimestamps.productGroupsLastSyncTimestamp)
                    .putImagesLastSyncTimestamp(clientCode, updatedLastSyncTimestamps.picturesLastSyncTimestamp)
                    .build()
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

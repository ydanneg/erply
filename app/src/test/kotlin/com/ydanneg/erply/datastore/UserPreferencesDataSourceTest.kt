package com.ydanneg.erply.datastore

import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.test.tempDir
import com.ydanneg.erply.test.testScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserPreferencesDataSourceTest {

    @get:Rule
    val tempDir = tempDir()
    private val testScope = testScope()

    private lateinit var dataStore: UserPreferencesDataSource
    private lateinit var userSessionDataSource: UserSessionDataSource

    @BeforeTest
    fun setup() {
        userSessionDataSource = UserSessionDataSource(tempDir.testUserSessionDataStore(testScope), TestDataStoreModule.fakeEncryptionManager())
        dataStore = UserPreferencesDataSourceImpl(
            tempDir.testUserPreferencesDataStore(testScope),
            userSessionDataSource
        )
    }

    @Test
    fun shouldHaveDefaultPreferencesByDefault() = testScope.runTest {
        dataStore.userPreferences.first() shouldBe DEFAULT_PREFS
    }

    @Test
    fun shouldChangeThemeSettingWithoutAffectingOtherSettings() = testScope.runTest {
        dataStore.userPreferences.first() shouldBe DEFAULT_PREFS

        dataStore.setDarkThemeConfig(DarkThemeConfig.DARK)
        dataStore.userPreferences.first() shouldBe DEFAULT_PREFS.copy(darkThemeConfig = DarkThemeConfig.DARK)

        dataStore.setDarkThemeConfig(DarkThemeConfig.LIGHT)
        dataStore.userPreferences.first() shouldBe DEFAULT_PREFS.copy(darkThemeConfig = DarkThemeConfig.LIGHT)


        dataStore.setDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM)
        dataStore.userPreferences.first() shouldBe DEFAULT_PREFS.copy(darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM)
    }

    @Test
    fun shouldChangeLastChangeVersionPerClientSettingWithoutAffectingOtherSettings() = testScope.runTest {
        dataStore.userPreferences.first() shouldBe DEFAULT_PREFS

        run {
            val clientCode = "client1"
            userSessionDataSource.updateUserSession(userSession(clientCode))
            val lastSyncTimestamps = randomLastSyncTimestamps()
            dataStore.updateChangeListVersion(clientCode) { lastSyncTimestamps }
            dataStore.userPreferences.first() shouldBe DEFAULT_PREFS.copy(lastSyncTimestamps = lastSyncTimestamps)
            userSessionDataSource.clear()
        }

        run {
            val clientCode = "client2"
            userSessionDataSource.updateUserSession(userSession(clientCode))
            val lastSyncTimestamps = randomLastSyncTimestamps()
            // TODO: remove client code from params, under the hood it still reads userSession that contains clientCode
            dataStore.updateChangeListVersion(clientCode) { lastSyncTimestamps }
            dataStore.userPreferences.first() shouldBe DEFAULT_PREFS.copy(lastSyncTimestamps = lastSyncTimestamps)
        }
    }

    private fun randomLastSyncTimestamps() = LastSyncTimestamps(
        productGroupsLastSyncTimestamp = Random.nextLong(),
        productsLastSyncTimestamp = Random.nextLong(),
        picturesLastSyncTimestamp = Random.nextLong()
    )

    private fun userSession(clientCode: String) = UserSession(
        userId = "userId",
        username = "username",
        clientCode = clientCode,
        token = "token",
        password = "password"
    )

    companion object {
        private val DEFAULT_PREFS = UserPreferences()
    }
}

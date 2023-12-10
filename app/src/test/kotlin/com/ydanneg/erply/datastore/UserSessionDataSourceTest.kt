package com.ydanneg.erply.datastore

import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.test.tempDir
import com.ydanneg.erply.test.testScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserSessionDataSourceTest {

    @get:Rule
    val tempDir = tempDir()
    private val testScope = testScope()

    private lateinit var dataStore: UserSessionDataSource


    @BeforeTest
    fun setup() {
        dataStore = UserSessionDataSource(
            tempDir.testUserSessionDataStore(testScope),
            TestDataStoreModule.fakeEncryptionManager()
        )
    }

    @Test
    fun shouldBeEmptyByDefault() = testScope.runTest {
        val userSession = dataStore.userSession.first()
        userSession shouldBe DEFAULT_SESSION
    }

    @Test
    fun shouldUpdateUserSession() = testScope.runTest {
        dataStore.updateUserSession(TEST_SESSION)

        val userSession = dataStore.userSession.first()
        userSession shouldBe TEST_SESSION
    }

    @Test
    fun shouldClear() = testScope.runTest {
        dataStore.updateUserSession(TEST_SESSION)
        dataStore.userSession.first() shouldBe TEST_SESSION

        dataStore.clear()
        dataStore.userSession.first() shouldBe DEFAULT_SESSION
    }


    companion object {
        private val DEFAULT_SESSION = UserSession(
            userId = "",
            username = "",
            clientCode = "",
            token = null,
            password = null
        )
        private val TEST_SESSION = UserSession(
            userId = "userId",
            username = "username",
            clientCode = "clientCode",
            token = "token",
            password = "password"
        )
    }

}

package com.ydanneg.erply.datastore

import com.ydanneg.erply.model.UserSession
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserSessionDataSourceTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var dataStore: UserSessionDataSource

    @TempDir
    private lateinit var tmpFolder: File

    @BeforeEach
    fun setup() {
        dataStore = UserSessionDataSource(
            tmpFolder.testUserSessionDataStore(testScope),
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

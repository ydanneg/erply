package com.ydanneg.erply.data.repository

import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.model.UserData
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.model.UserSession
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDataRepositoryImplTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun `userData should combine userSession and userPreferences`() = testScope.runTest {
        val userSessionRepository = mockk<UserSessionRepository>(relaxed = true)
        coEvery { userSessionRepository.userSession } returns flowOf(userSession)

        val userPreferencesDataSource = mockk<UserPreferencesDataSource>(relaxed = true)
        coEvery { userPreferencesDataSource.userPreferences } returns flowOf(userPreferences)

        val userDataRepository = UserDataRepositoryImpl(
            userSessionRepository = userSessionRepository,
            userPreferencesDataSource = userPreferencesDataSource
        )

        userDataRepository.userData.first() shouldBe UserData(userSession, userPreferences)

        coVerify(exactly = 1) { userSessionRepository.userSession }
        coVerify(exactly = 1) { userPreferencesDataSource.userPreferences }

        confirmVerified(userSessionRepository, userPreferencesDataSource)
    }

    companion object {
        private val userSession = UserSession(
            clientCode = "clientCode",
            userId = "userId",
            username = "username",
            token = "token",
            password = "password"
        )

        private val userPreferences = UserPreferences()
    }
}

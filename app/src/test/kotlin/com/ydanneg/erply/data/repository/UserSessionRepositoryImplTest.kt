package com.ydanneg.erply.data.repository

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.api.model.ErplyVerifiedUser
import com.ydanneg.erply.datastore.TestDataStoreModule
import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.datastore.testUserSessionDataStore
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.network.api.toModel
import com.ydanneg.erply.test.tempDir
import com.ydanneg.erply.test.testScope
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserSessionRepositoryImplTest {

    @get:Rule
    val tempDir = tempDir()
    private val testScope = testScope()

    private lateinit var userSessionDataSource: UserSessionDataSource

    @BeforeTest
    fun setup() {
        userSessionDataSource = UserSessionDataSource(
            userSessionDataStore = tempDir.testUserSessionDataStore(testScope),
            encryptionManager = TestDataStoreModule.fakeEncryptionManager()
        )
    }

    @Test
    fun `userSession actually comes from UserSessionDataSource`() = testScope.runTest {
        val networkDataSourceMock = mockk<ErplyNetworkDataSource>(relaxed = true)
        val userSessionDataSourceMock = mockk<UserSessionDataSource>(relaxed = true)
        coEvery { userSessionDataSourceMock.userSession } returns flowOf(userSession)

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSourceMock,
            userSessionDataSource = userSessionDataSourceMock
        )

        userSessionRepository.userSession.first() shouldBe userSession

        coVerify(exactly = 1) { userSessionDataSourceMock.userSession }

        confirmVerified(userSessionDataSourceMock, networkDataSourceMock)
    }

    @Test
    fun `login should return user session based on Erply API verifyUser response`() = testScope.runTest {
        val networkDataSource = mockk<ErplyNetworkDataSource>()
        coEvery {
            networkDataSource.login(
                userSession.clientCode,
                userSession.username,
                userSession.password!!
            )
        } returns verifiedUser

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource
        )

        userSessionRepository.login(userSession.clientCode, userSession.username, userSession.password!!)
        userSessionRepository.userSession.first() shouldBe userSession

        coVerify(exactly = 1) { networkDataSource.login(userSession.clientCode, userSession.username, userSession.password!!) }
        confirmVerified(networkDataSource)
    }

    @Test
    fun `tryLogin should call underlying network source if session exists`() = testScope.runTest {
        val networkDataSource = mockk<ErplyNetworkDataSource>()
        coEvery {
            networkDataSource.login(
                userSession.clientCode,
                userSession.username,
                userSession.password!!
            )
        } returns verifiedUser

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )

        // set current session
        userSessionDataSource.updateUserSession(userSession)
        // try login using current session
        userSessionRepository.tryLogin()

        // verify networkDataSource is called correctly
        coVerify(exactly = 1) {
            networkDataSource.login(
                userSession.clientCode,
                userSession.username,
                userSession.password!!
            )
        }
        confirmVerified(networkDataSource)
    }

    @Test
    fun `tryAuthenticateUnauthorized should retry provided lambda on Unauthorized ApiException`() = testScope.runTest {
        val networkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        coEvery { networkDataSource.login(userSession.clientCode, userSession.username, userSession.password!!) } returns verifiedUser

        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true)
        coEvery { userSessionDataSource.userSession } returns flowOf(userSession)

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )

        var attempts = 0
        userSessionRepository.tryAuthenticateUnauthorized(true) { _ ->
            if (attempts++ == 0) throw ErplyApiException(ErplyApiError.Unauthorized)
        }

        coVerify(exactly = 1) { networkDataSource.login(userSession.clientCode, userSession.username, userSession.password!!) }
        coVerify(exactly = 1) { userSessionDataSource.userSession }
        coVerify(exactly = 1) { userSessionDataSource.updateUserSession(userSession) }

        confirmVerified(networkDataSource, userSessionDataSource)
    }

    @Test
    fun `logout should always clear user session`() = testScope.runTest {
        val networkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true)

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )

        userSessionRepository.logout()

        coVerify(exactly = 1) { userSessionDataSource.userSession }
        coVerify(exactly = 1) { userSessionDataSource.clear() }

        confirmVerified(userSessionDataSource, networkDataSource)
    }

    @Test
    fun `withClientCode should return clientCode from user session`() = testScope.runTest {
        val networkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)

        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true)
        coEvery { userSessionDataSource.userSession } returns flowOf(userSession)

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )

        val withClientCode = userSessionRepository.withClientCode { code ->
            flowOf(code)
        }

        withClientCode.first() shouldBe userSession.clientCode

        coVerify(exactly = 1) { userSessionDataSource.userSession }
        confirmVerified(networkDataSource, userSessionDataSource)
    }

    companion object {
        private val verifiedUser = ErplyVerifiedUser(
            userId = "userId",
            username = "username",
            name = "name",
            groupName = "groupName",
            token = "token"
        )
        private val userSession = verifiedUser.toModel("client", "pwd")

    }
}

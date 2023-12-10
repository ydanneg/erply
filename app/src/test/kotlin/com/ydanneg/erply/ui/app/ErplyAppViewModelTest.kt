package com.ydanneg.erply.ui.app

import app.cash.turbine.test
import com.ydanneg.erply.model.UserData
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.test.MainDispatcherRule
import com.ydanneg.erply.test.doubles.FakeUserDataRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test

class ErplyAppViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ErplyAppViewModel

    @BeforeTest
    fun setup() {
        viewModel = ErplyAppViewModel(FakeUserDataRepository(fakeUserData))
    }

    @Test
    fun `should produce user data from repository`() = runTest {
        viewModel.userData.test {
            awaitItem() shouldBe fakeUserData
        }
    }

    companion object {
        private val fakeUserData = UserData(
            UserSession(
                clientCode = "clientCode",
                username = "username",
                userId = "userId",
                token = "token",
                password = "password"
            ),
            UserPreferences()
        )
    }
}

package com.ydanneg.erply.ui.screens.login

import app.cash.turbine.test
import com.ydanneg.erply.test.MainDispatcherRule
import com.ydanneg.erply.test.doubles.FakeUserPreferencesDataSource
import com.ydanneg.erply.test.doubles.FakeUserSessionRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test

class LoginScreenViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginScreenViewModel

    @BeforeTest
    fun setup() {
        viewModel = LoginScreenViewModel(FakeUserSessionRepository, FakeUserPreferencesDataSource)
    }

    @Test
    fun `login should change state to Loading and then to Success after completion`() = runTest {
        viewModel.uiState.test {
            awaitItem().shouldBeInstanceOf<LoginUIState.Idle>()
            viewModel.doLogin("testClient", "testUsername", "testPassword")
            awaitItem().shouldBeInstanceOf<LoginUIState.Loading>()
            awaitItem().shouldBeInstanceOf<LoginUIState.LoggedIn>()
        }
    }

    @Test
    fun `setKeepMeSignedIn should change user preferences`() = runTest {
        viewModel.keepMeSignedIn.test {
            awaitItem() shouldBe false
            viewModel.setKeepMeLoggedIn(true)
            awaitItem() shouldBe true
            viewModel.setKeepMeLoggedIn(false)
            awaitItem() shouldBe false
        }
    }
}

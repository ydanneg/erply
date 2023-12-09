package com.ydanneg.erply.ui.screens.login

import app.cash.turbine.test
import com.ydanneg.erply.test.CoroutinesTestExtension
import com.ydanneg.erply.test.FakeUserPreferencesDataSource
import com.ydanneg.erply.test.FakeUserSessionRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class LoginScreenViewModelTest {

    private lateinit var viewModel: LoginScreenViewModel

    @BeforeEach
    fun setup() {
        viewModel = LoginScreenViewModel(FakeUserSessionRepository, FakeUserPreferencesDataSource)
    }

    @Test
    fun `login should change state to Loading and then to Success after completion`() = runTest {
        viewModel.uiState.test {
            awaitItem().shouldBeInstanceOf<LoginUIState.Idle>()
            viewModel.doLogin("", "", "")
            awaitItem().shouldBeInstanceOf<LoginUIState.Loading>()
            awaitItem().shouldBeInstanceOf<LoginUIState.Success>()
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

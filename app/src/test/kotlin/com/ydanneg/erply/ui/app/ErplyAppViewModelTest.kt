package com.ydanneg.erply.ui.app

import app.cash.turbine.test
import com.ydanneg.erply.model.UserData
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.test.CoroutinesTestExtension
import com.ydanneg.erply.test.doubles.FakeUserDataRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class ErplyAppViewModelTest {

    private lateinit var viewModel: ErplyAppViewModel

    @BeforeEach
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

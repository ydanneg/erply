package erply.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import erply.model.UserData
import erply.model.UserPreferences
import erply.model.isLoggedIn
import erply.ui.components.ExitConfirmation
import erply.ui.screens.login.LoginScreen
import erply.ui.screens.main.MainScreen

data class ErplyAppState(val userData: UserData)

@Composable
fun rememberAppState(
    user: UserData = UserData(null, UserPreferences())
): ErplyAppState = remember(user) { ErplyAppState(user) }

@Composable
fun ErplyApp(viewModel: ErplyAppViewModel) {
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val appState = rememberAppState(userData)

    ExitConfirmation()

    Surface(color = MaterialTheme.colorScheme.background) {
        ErplyAppContent(
            appState = appState
        )
    }
}

@Composable
private fun ErplyAppContent(appState: ErplyAppState) {
    Column(Modifier.fillMaxSize()) {
        if (appState.userData.session?.isLoggedIn() == true) {
            MainScreen(hiltViewModel(), appState)
        } else {
            LoginScreen(hiltViewModel())
        }
    }
}
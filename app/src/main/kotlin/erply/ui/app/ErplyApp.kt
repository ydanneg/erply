package erply.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import erply.ui.components.ExitConfirmation
import erply.ui.screens.login.LoginScreen
import erply.ui.screens.main.MainScreen

data class LoggedInUser(
    val userId: String, val username: String, val token: String
)

@Stable
data class ErplyAppState(val loggedInUser: LoggedInUser? = null)

fun ErplyAppState.isLoggedIn() = loggedInUser != null

@Composable
fun rememberAppState(user: LoggedInUser? = null): ErplyAppState = remember(user) {
    ErplyAppState(user)
}

@Composable
fun ErplyApp(viewModel: ErplyAppViewModel) {
    val loggedInUser by viewModel.loggedInUser.collectAsStateWithLifecycle()
    val appState = rememberAppState(loggedInUser)

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
        if (!appState.isLoggedIn()) LoginScreen(hiltViewModel())
        else MainScreen(hiltViewModel(), appState)
    }
}
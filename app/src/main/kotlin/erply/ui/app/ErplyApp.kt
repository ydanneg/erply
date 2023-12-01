package erply.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import erply.ui.screens.login.LoginScreen
import erply.ui.screens.login.LoginScreenViewModel
import erply.ui.screens.products.ProductsScreen
import erply.ui.screens.products.ProductsScreenViewModel

class ErplyAppState(
    isLoggedIn: Boolean = false,
    username: String = "Guest"
) {
    val isLoggedIn by mutableStateOf(isLoggedIn)
    val username by mutableStateOf(username)
}

@Composable
fun rememberMainScreenState(
    isLoggedIn: Boolean = false,
    username: String = "Guest"
): ErplyAppState {
    return remember(isLoggedIn, username) {
        ErplyAppState(isLoggedIn, username)
    }
}

@Composable
fun ErplyApp(viewModel: ErplyAppViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val username by viewModel.username.collectAsState()

    val appState = rememberMainScreenState(isLoggedIn, username)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { ErplyAppContent(it, appState, onLogout = { viewModel.logOut() }) }
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ErplyAppContent(padding: PaddingValues, state: ErplyAppState, onLogout: () -> Unit = {}) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .consumeWindowInsets(padding)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            ),
    ) {
        Column(Modifier.fillMaxSize()) {
            if (!state.isLoggedIn) {
                val loginViewModel = hiltViewModel<LoginScreenViewModel>()
                LoginScreen(viewModel = loginViewModel)
            } else {
                val productsViewModel = hiltViewModel<ProductsScreenViewModel>()
                ProductsScreen(viewModel = productsViewModel)
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(text = state.username, style = MaterialTheme.typography.displaySmall)
//                        Button(onClick = onLogout) {
//                            Text(text = "Log out")
//                        }
//                    }
//                }
            }
        }
    }
}
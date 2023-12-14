package com.ydanneg.erply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ydanneg.erply.MainActivityUiState.Loading.isInitialized
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.ui.app.ErplyApp
import com.ydanneg.erply.ui.app.ErplyAppViewModel
import com.ydanneg.erply.ui.theme.ErplyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.onEach {
                    delay(100)
                    uiState = it
                }.collect()
            }
        }

        splashScreen.setKeepOnScreenCondition { !uiState.isInitialized() }

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)
//            DisposableEffect(darkTheme) {
//                enableEdgeToEdge(
//                    statusBarStyle = SystemBarStyle.auto(
//                        Color.TRANSPARENT,
//                        Color.TRANSPARENT,
//                    ) { darkTheme }
//                )
//                onDispose {}
//            }
            ErplyTheme(useDarkTheme = darkTheme) {
                val appViewModel by viewModels<ErplyAppViewModel>()
                ErplyApp(viewModel = appViewModel)
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState,
) = when (uiState) {
    MainActivityUiState.Loading -> isSystemInDarkTheme()
    is MainActivityUiState.Success -> when (uiState.userData.prefs.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

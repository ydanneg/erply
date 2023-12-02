package erply.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import erply.ui.components.ExitConfirmation

@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()

    ExitConfirmation()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MainBottomNavigationBar(mainNavController) },
        content = {
            NavHost(
                modifier = Modifier.padding(it),
                navController = mainNavController,
                startDestination = Screen.TopLevel.Catalog.route
            ) {
                mainNavGraph(mainNavController)
            }
        }
    )
}

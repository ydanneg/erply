package erply.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MainBottomNavigationBar(navController) },
        content = {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = Screen.TopLevel.Catalog.route
            ) {
                mainNavGraph(navController)
            }
        }
    )
}

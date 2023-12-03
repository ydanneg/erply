package erply.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import erply.ui.app.ErplyAppState
import erply.ui.app.rememberAppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Stable
class MainScreenState(
    val appState: ErplyAppState,
    val drawerState: DrawerState,
    val scope: CoroutineScope,
    val navController: NavHostController
)

@Composable
fun rememberMainScreenState(
    appState: ErplyAppState = rememberAppState(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): MainScreenState {
    return remember(appState, drawerState, scope, navController) {
        MainScreenState(
            appState = appState,
            drawerState = drawerState,
            scope = scope,
            navController = navController
        )
    }
}

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    appState: ErplyAppState = rememberAppState()
) {
    val mainScreenState = rememberMainScreenState(
        appState = appState,
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
        scope = rememberCoroutineScope(),
        navController = rememberNavController()
    )

    BackHandler(mainScreenState.drawerState.isOpen) {
        mainScreenState.scope.launch {
            mainScreenState.drawerState.close()
        }
    }

    ErplyMainNavigationDrawer(
        state = mainScreenState,
        onLogout = { viewModel.logOut() }
    ) {
        NavHost(
            navController = mainScreenState.navController,
            startDestination = Screen.TopLevel.Catalog.route
        ) {
            mainNavGraph(mainScreenState)
        }
    }
}

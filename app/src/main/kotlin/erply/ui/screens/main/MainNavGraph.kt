package erply.ui.screens.main

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import erply.ui.screens.main.groups.ProductGroupsScreen
import erply.ui.screens.main.products.ProductsScreen

val topLevelScreens = listOf(Screen.TopLevel.Catalog, Screen.TopLevel.Profile)

@Composable
fun MainBottomNavigationBar(nav: NavHostController) {
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val isTopLevelMainScreen by remember(navBackStackEntry) {
        derivedStateOf { navBackStackEntry?.destination?.route?.let { route -> topLevelScreens.any { it.route == route } } == true }
    }

    if (isTopLevelMainScreen) {
        NavigationBar {
            val currentDestination = navBackStackEntry?.destination
            topLevelScreens.forEach { screen ->
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(),
                    icon = { Icon(screen.icon, contentDescription = screen.label) },
                    label = { Text(screen.label) },
                    selected = currentDestination?.route == screen.route,
                    onClick = { nav.navigateToTopLevel(screen.route) }
                )
            }
        }
    }
}

fun NavGraphBuilder.mainNavGraph(
    nav: NavHostController
) {
    screen(Screen.TopLevel.Catalog) {
        ProductGroupsScreen(
            viewModel = hiltViewModel(),
            onGroupSelected = { nav.navigate(Screen.ProductGroup.view(it)) }
        )
    }
    screen(Screen.TopLevel.Profile) {
        Text(text = "Profile screen")
    }
    screen(Screen.ProductGroup) {
        ProductsScreen(
            viewModel = hiltViewModel(),
            onBack = { nav.popBackStack() }
        )
    }
}

fun NavGraphBuilder.screen(screen: Screen, content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit) =
    composable(
        route = screen.route,
        content = content
    )

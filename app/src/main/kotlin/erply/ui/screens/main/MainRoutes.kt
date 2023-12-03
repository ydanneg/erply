package erply.ui.screens.main

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

sealed class Screen(
    open val route: String,
    open val label: String,
) {
    sealed class TopLevel(
        override val route: String,
        override val label: String,
        val icon: ImageVector,
    ) : Screen(route, label) {
        data object Catalog : TopLevel("catalog", "Catalog", Icons.Filled.Home)

        data object Settings : TopLevel("settings", "Settings", Icons.Filled.Settings)
    }


    data object ProductGroup : Screen(
        route = "group/{groupId}",
        label = "Product"
    ) {
        fun view(groupId: String) = "group/$groupId"
    }
}

fun NavHostController.navigate(screen: Screen, builder: NavOptionsBuilder.() -> Unit = {}) = navigate(screen.route) { builder(this) }
fun NavHostController.navigateToTopLevel(route: String, restoreState: Boolean = true, saveState: Boolean = true) {
    val findStartDestination = graph.findStartDestination()
    Log.d("Nav", "navigating to $route, $restoreState, $findStartDestination")
    Log.d("Nav", "currentBackStackEntry: ${currentBackStackEntry?.destination?.route}")
    Log.d("Nav", "previousBackStackEntry: ${previousBackStackEntry?.destination?.route}")
    navigate(route) {
        popUpTo(findStartDestination.id) {
            this.saveState = saveState
//            inclusive = true
        }
//        launchSingleTop = true
        this.restoreState = restoreState
    }
}

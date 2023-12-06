package com.ydanneg.erply.ui.screens.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.ydanneg.erply.R

sealed class Screen(
    open val route: String,
    @StringRes open val label: Int,
) {
    sealed class TopLevel(
        override val route: String,
        override val label: Int,
        val icon: ImageVector,
    ) : Screen(route, label) {
        data object Catalog : TopLevel("catalog", R.string.route_catalog_label, Icons.Filled.Home)//NON-NLS

        data object Settings : TopLevel("settings", R.string.route_settings_label, Icons.Filled.Settings)//NON-NLS
    }


    data object ProductGroup : Screen(
        route = "group/{groupId}",//NON-NLS
        label = R.string.route_group_label
    ) {
        fun view(groupId: String) = "group/$groupId"//NON-NLS
    }
}

fun NavHostController.navigate(screen: Screen, builder: NavOptionsBuilder.() -> Unit = {}) = navigate(screen.route) { builder(this) }

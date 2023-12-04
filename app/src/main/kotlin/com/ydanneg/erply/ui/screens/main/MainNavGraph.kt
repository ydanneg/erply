package com.ydanneg.erply.ui.screens.main

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ydanneg.erply.ui.screens.main.catalog.ProductGroupsScreen
import com.ydanneg.erply.ui.screens.main.products.ProductsScreen
import com.ydanneg.erply.ui.screens.main.settings.SettingsScreen
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes
import kotlinx.coroutines.launch

private val drawerItems = listOf(Screen.TopLevel.Catalog, Screen.TopLevel.Settings).map {
    DrawerItem(
        label = it.label,
        icon = it.icon,
        route = it
    )
}

private data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val route: Screen
)

@PreviewThemes
@Composable
private fun DrawerContentPreview() {
    ErplyThemePreviewSurface {
        DrawerContent(username = "ydanneg@gmail.com", items = drawerItems, selectedItem = drawerItems.first(), onItemClick = {})
    }
}

@Composable
private fun DrawerContent(
    username: String,
    items: List<DrawerItem>,
    selectedItem: DrawerItem? = null,
    onItemClick: (DrawerItem) -> Unit,
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(64.dp),
                    imageVector = Icons.Filled.AccountCircle,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            Text(
                text = username,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogout) {
                Text(text = "Log out")
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 16.dp))

        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(text = item.label) },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                selected = item == selectedItem,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun ErplyMainNavigationDrawer(
    state: MainScreenState = rememberMainScreenState(),
    onLogout: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var selectedItem by remember { mutableStateOf(drawerItems[0]) }
    val navBackStackEntry by state.navController.currentBackStackEntryAsState()

    ModalNavigationDrawer(
        drawerState = state.drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    username = state.appState.userData.session?.username ?: "Guest",
                    items = drawerItems,
                    selectedItem = navBackStackEntry?.destination?.route?.let { route -> drawerItems.find { it.route.route == route } },
                    onItemClick = { item ->
                        state.scope.launch { state.drawerState.close() }
                        selectedItem = item
                        state.navController.navigate(item.route)
                    },
                    onLogout = onLogout
                )
            }
        },
        content = content
    )
}

fun NavGraphBuilder.mainNavGraph(
    mainScreenState: MainScreenState
) {
    screen(Screen.TopLevel.Catalog) {
        ProductGroupsScreen(
            viewModel = hiltViewModel(),
            onGroupSelected = { mainScreenState.navController.navigate(Screen.ProductGroup.view(it)) },
            mainScreenState = mainScreenState
        )
    }
    screen(Screen.TopLevel.Settings) {
        SettingsScreen(
            viewModel = hiltViewModel(),
            mainScreenState = mainScreenState
        )
    }
    screen(Screen.ProductGroup) {
        ProductsScreen(
            mainScreenState = mainScreenState,
            viewModel = hiltViewModel()
        )
    }
}

private fun NavGraphBuilder.screen(screen: Screen, content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit) =
    composable(
        route = screen.route,
        content = content
    )

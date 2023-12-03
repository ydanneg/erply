package erply.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import erply.ui.theme.ErplyThemePreviewSurface
import erply.ui.theme.PreviewThemes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
@PreviewThemes
private fun ErplyDrawerTopAppbarPreview() = ErplyThemePreviewSurface {
    ErplyDrawerTopAppbar("This is a title")
}

@Composable
@PreviewThemes
private fun ErplyNavTopAppbarPreview() = ErplyThemePreviewSurface {
    ErplyNavTopAppbar("This is a title")
}

@Composable
private fun TopAppbarTitle(title: String) = Text(title)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErplySearchableTopAppbar(
    title: String,
    searchQuery: String? = null,
    onSearch: (String?) -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
) {

    var searching by rememberSaveable {
        mutableStateOf(searchQuery?.isNotBlank() == true)
    }

    BackHandler(enabled = searching) {
        searching = false
        onSearch(null)
    }

    CenterAlignedTopAppBar(
        navigationIcon = {
            if (!searching) navigationIcon()
        },
        title = { TopAppbarTitle(title) },
        actions = {
            if (searching) {
                SearchBar(
                    searchText = searchQuery ?: "",
                    onSearchTextChanged = {
                        onSearch(it)
                    },
                    onCloseClick = {
                        searching = false
                        onSearch(null)
                    }
                )
            } else {
                IconButton(onClick = { searching = true }) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErplyTopAppbar(
    title: String,
    navigationIcon: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        navigationIcon = navigationIcon,
        title = { TopAppbarTitle(title) }
    )
}

@Composable
fun ErplyDrawerTopAppbar(
    title: String,
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    ErplyTopAppbar(
        title = title,
        navigationIcon = {
            IconButton(
                onClick = { scope.launch { if (drawerState.isOpen) drawerState.close() else drawerState.open() } }
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "")
            }
        }
    )
}

@Composable
fun ErplyNavTopAppbar(
    title: String,
    searchQuery: String? = null,
    onSearch: (String?) -> Unit = {},
    navController: NavController = rememberNavController()
) {
    ErplySearchableTopAppbar(
        title = title,
        searchQuery = searchQuery,
        onSearch = onSearch,
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }
        }
    )
}
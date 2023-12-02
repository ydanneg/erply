package erply.ui.screens.main.groups

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ydanneg.erply.model.ErplyProductGroup
import com.ydanneg.erply.model.LocalizedValue
import erply.ui.theme.ErplyThemePreviewSurface
import erply.ui.theme.PreviewThemes


@OptIn(ExperimentalMaterial3Api::class)
@PreviewThemes
@Composable
fun ProductGroupPreview() {
    val groups = (1L..15L).mapIndexed { index, item ->
        ErplyProductGroup(
            id = item.toString(),
            name = LocalizedValue("name$item"),
            parentId = "0",
            order = index,
            description = LocalizedValue("description$item"),
            changed = (System.currentTimeMillis() / 1000).toInt()
        )
    }

    ErplyThemePreviewSurface {
        ProductGroupsScreenContent(groups = groups)
    }
}

const val TAG = "ProductGroupsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductGroupsScreen(
    viewModel: ProductGroupsScreenViewModel,
    onGroupSelected: (String) -> Unit = {}
) {
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pullToRefreshState = rememberPullToRefreshState(enabled = { true })

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            viewModel.loadGroups()
        }
    }

    LaunchedEffect(uiState, pullToRefreshState) {
        when (uiState) {
//            ProductGroupsScreenUiState.Loading -> pullToRefreshState.startRefresh()
            ProductGroupsScreenUiState.Success -> pullToRefreshState.endRefresh()
            else -> {}
        }
    }

    val context = LocalContext.current
    if (uiState is ProductGroupsScreenUiState.Error) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT).show()
        }
    }

    ProductGroupsScreenContent(
        groups = groups,
        onLogOut = { viewModel.logOut() },
        onGroupClicked = onGroupSelected,
        pullToRefreshState = pullToRefreshState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductGroupsScreenContent(
    groups: List<ErplyProductGroup>,
    isLoading: Boolean = false,
    onRefresh: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onGroupClicked: (String) -> Unit = {},
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(enabled = { true })
) {
    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(groups, key = { it.id }) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clickable { onGroupClicked(it.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it.name.en, textAlign = TextAlign.Center)
                }
            }
        }
        PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    val context = LocalContext.current
    Toast.makeText(context, message, duration).show()
}

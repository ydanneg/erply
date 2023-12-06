package com.ydanneg.erply.ui.screens.main.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.LocalizedValue
import com.ydanneg.erply.ui.components.ErplyDrawerTopAppbar
import com.ydanneg.erply.ui.components.Loading
import com.ydanneg.erply.ui.components.NothingToShow
import com.ydanneg.erply.ui.screens.main.MainScreenState
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes


@OptIn(ExperimentalMaterial3Api::class)
@PreviewThemes
@Composable
private fun ProductGroupPreview() {
    val groups = (1L..15L).mapIndexed { index, item ->
        ErplyProductGroup(
            id = item.toString(),
            name = LocalizedValue("name$item"),
            parentId = "0",
            order = index,
            description = LocalizedValue("description$item"),
            changed = System.currentTimeMillis() / 1000
        )
    }

    ErplyThemePreviewSurface {
        ProductGroupsScreenContent(groups = groups)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewThemes
@Composable
private fun ProductGroupEmptyPreview() {
    ErplyThemePreviewSurface {
        ProductGroupsScreenContent(groups = listOf())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductGroupsScreen(
    mainScreenState: MainScreenState,
    viewModel: ProductGroupsScreenViewModel, onGroupSelected: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pullToRefreshState = rememberPullToRefreshState(enabled = { !uiState.isLoading })

    if (pullToRefreshState.isRefreshing) {
        DisposableEffect(Unit) {
            viewModel.loadProductGroups()
            onDispose { }
        }
    }

    DisposableEffect(uiState) {
        if (uiState.isLoading) pullToRefreshState.startRefresh() else pullToRefreshState.endRefresh()
        onDispose { }
    }

    ProductGroupsScreenContent(
        groups = uiState.groups,
        isLoading = uiState.isLoading,
        onGroupClicked = onGroupSelected,
        pullToRefreshState = pullToRefreshState,
        drawerState = mainScreenState.drawerState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductGroupsScreenContent(
    groups: List<ErplyProductGroup>,
    isLoading: Boolean = false,
    onGroupClicked: (String) -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(enabled = { true })
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ErplyDrawerTopAppbar(
                title = "Catalog",
                drawerState = drawerState
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                if (groups.isEmpty()) if (isLoading) Loading() else NothingToShow()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(groups, key = { it.id }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 56.dp)
                                .clickable { onGroupClicked(it.id) }
                                .background(MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.extraSmall),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = it.name.en.uppercase(), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }

                PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    )
}

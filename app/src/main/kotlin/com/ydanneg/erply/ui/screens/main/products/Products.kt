package com.ydanneg.erply.ui.screens.main.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.ErplyProductType
import com.ydanneg.erply.api.model.LocalizedValue
import com.ydanneg.erply.ui.components.ErplyNavTopAppbar
import com.ydanneg.erply.ui.screens.main.MainScreenState
import com.ydanneg.erply.ui.screens.main.products.ProductsScreenUiState.Success.isLoading
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes
import com.ydanneg.erply.ui.util.generateAlphanumeric

@OptIn(ExperimentalMaterial3Api::class)
@PreviewThemes
@Composable
private fun ProductsScreenContentPreview() {
    val products = (1L..15L).map {
        ErplyProduct(
            id = it.toString(),
            name = LocalizedValue("$it ${generateAlphanumeric()}"),
            groupId = it.toString(),
            price = "19.99",
            type = ErplyProductType.PRODUCT,
            description = LocalizedValue("description$it"),
            changed = (System.currentTimeMillis() / 100).toInt()
        )
    }
    ErplyThemePreviewSurface {
        ProductsScreenContent(
            group = ErplyProductGroup(
                id = "",
                parentId = "",
                order = 1,
                name = LocalizedValue("Very long group name, da, da, dadadada"),
                description = null,
                changed = 0
            ),
            products = products
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    mainScreenState: MainScreenState,
    viewModel: ProductsScreenViewModel
) {
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val group by viewModel.group.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val pullToRefreshState = rememberPullToRefreshState(enabled = { !uiState.isLoading() })

    if (pullToRefreshState.isRefreshing) {
        DisposableEffect(Unit) {
            viewModel.loadProducts()
            onDispose { }
        }
    }

    LaunchedEffect(uiState, pullToRefreshState) {
        when (uiState) {
            is ProductsScreenUiState.Loading -> pullToRefreshState.startRefresh()
            else -> pullToRefreshState.endRefresh()
        }
    }

    ProductsScreenContent(
        group = group,
        products = products,
        searchQuery = searchQuery,
        onSearch = viewModel::setSearchQuery,
        navController = mainScreenState.navController,
        pullToRefreshState = pullToRefreshState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreenContent(
    group: ErplyProductGroup?,
    products: List<ErplyProduct>,
    searchQuery: String? = null,
    onSearch: (String?) -> Unit = {},
    navController: NavController = rememberNavController(),
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(enabled = { true })
) {
    val placeholder = rememberVectorPainter(Icons.Filled.ImageNotSupported)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ErplyNavTopAppbar(
                title = group?.name?.en ?: "",
                searchQuery = searchQuery,
                onSearch = onSearch,
                navController = navController
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                if (products.isNotEmpty()) {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(minSize = 128.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products, key = { it.id }) {
                            Card(
                                modifier = Modifier
                                    .size(156.dp)
                                    .clickable { },
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.size(64.dp),
                                        // FIXME
                                        model = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Bitcoin.svg/1200px-Bitcoin.svg.png",
                                        contentDescription = it.name.en,
                                        placeholder = placeholder
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = it.name.en,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.weight(1.0f))
                                    Text(
                                        text = "\$${it.price}",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "syncing...", style = MaterialTheme.typography.titleLarge)
                    }
                }
                PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    )
}

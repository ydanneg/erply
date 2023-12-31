package com.ydanneg.erply.ui.screens.main.products

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.twotone.ImageNotSupported
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.ydanneg.erply.data.repository.SortingOrder
import com.ydanneg.erply.model.ProductGroup
import com.ydanneg.erply.model.ProductWithImage
import com.ydanneg.erply.ui.components.ErplyNavTopAppbar
import com.ydanneg.erply.ui.components.FadedLinerProgressIndicator
import com.ydanneg.erply.ui.components.Loading
import com.ydanneg.erply.ui.components.NothingToShow
import com.ydanneg.erply.ui.components.VSpace
import com.ydanneg.erply.ui.components.WSpace
import com.ydanneg.erply.ui.screens.main.MainScreenState
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes
import com.ydanneg.erply.ui.util.generateAlphanumeric
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    mainScreenState: MainScreenState,
    viewModel: ProductsScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val products = viewModel.filteredProducts.collectAsLazyPagingItems()

    val pullToRefreshState = rememberPullToRefreshState(enabled = { !uiState.isLoading })

    LaunchedEffect(uiState) {
        if (uiState.notLoaded) {
            mainScreenState.navController.popBackStack()
        }
    }

    if (pullToRefreshState.isRefreshing) {
        DisposableEffect(Unit) {
            viewModel.loadProducts()
            onDispose { }
        }
    }

    DisposableEffect(uiState) {
        if (!uiState.isLoading) pullToRefreshState.endRefresh()
        onDispose { }
    }

    ProductsScreenContent(
        isLoading = uiState.isLoading,
        group = uiState.group,
        pagingProducts = products,
        searchQuery = uiState.searchQuery,
        onSearch = viewModel::setSearchQuery,
        sortingOrder = uiState.sortOder,
        onSortingOrderChanged = viewModel::setSortingOder,
        navController = mainScreenState.navController,
        pullToRefreshState = pullToRefreshState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreenContent(
    isLoading: Boolean = false,
    group: ProductGroup? = null,
    pagingProducts: LazyPagingItems<ProductWithImage> = emptyLazyPagingItems(),
    searchQuery: String? = null,
    onSearch: (String?) -> Unit = {},
    sortingOrder: SortingOrder,
    onSortingOrderChanged: (SortingOrder) -> Unit = {},
    navController: NavController = rememberNavController(),
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(enabled = { true })
) {
    val scope = rememberCoroutineScope()
    val placeholder = rememberVectorPainter(Icons.TwoTone.ImageNotSupported)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val lazyGridState = rememberLazyGridState()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val handleSortingOrderChange: (sortingOrder: SortingOrder) -> Unit = { order ->
        Log.i("SORTING", "handleSortingOrderChange: $order")
        onSortingOrderChanged(order)
        scope.launch {
            scaffoldState.bottomSheetState.hide()
            lazyGridState.scrollToItem(0)
        }.run { }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ErplyNavTopAppbar(
                title = group?.name ?: "",
                searchQuery = searchQuery,
                onSearch = onSearch,
                navController = navController,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.apply {
                                if (currentValue == SheetValue.Expanded) hide() else expand()
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "")
                    }
                }
            )
        },
        sheetContent = BottomSheetContent(
            sortingOrder = sortingOrder,
            handleSortingOrderChange = handleSortingOrderChange
        ),
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                if (pagingProducts.itemCount == 0) if (isLoading) Loading() else NothingToShow()

                Column(Modifier.padding(16.dp)) {
                    FadedLinerProgressIndicator(2.dp, visible = isLoading)
                    VSpace(4.dp)
                    LazyVerticalGrid(
                        state = lazyGridState,
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(minSize = 128.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            pagingProducts.itemCount,
                            key = pagingProducts.itemKey { it.id }
                        ) { index ->
                            val item = pagingProducts[index]
                            ProductCard(item, placeholder)
                        }
                    }
                }
                PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    )
}

@Composable
private fun BottomSheetContent(
    sortingOrder: SortingOrder,
    handleSortingOrderChange: (sortingOrder: SortingOrder) -> Unit
): @Composable (ColumnScope.() -> Unit) = {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .wrapContentHeight()
    ) {
        BottomSheetSortItem("Lowest price first", sortingOrder == SortingOrder.PRICE_ASC) {
            handleSortingOrderChange(SortingOrder.PRICE_ASC)
        }
        BottomSheetSortItem("Highest price first", sortingOrder == SortingOrder.PRICE_DESC) {
            handleSortingOrderChange(SortingOrder.PRICE_DESC)
        }
        BottomSheetSortItem("Name A → Z", sortingOrder == SortingOrder.NAME_ASC) {
            handleSortingOrderChange(SortingOrder.NAME_ASC)
        }
        BottomSheetSortItem("Name Z → A", sortingOrder == SortingOrder.NAME_DESC) {
            handleSortingOrderChange(SortingOrder.NAME_DESC)
        }
        BottomSheetSortItem("Newest first", sortingOrder == SortingOrder.CHANGE_DESC) {
            handleSortingOrderChange(SortingOrder.CHANGE_DESC)
        }
        BottomSheetSortItem("Oldest first", sortingOrder == SortingOrder.CHANGE_ASC) {
            handleSortingOrderChange(SortingOrder.CHANGE_ASC)
        }
    }
}

@Composable
private fun BottomSheetSortItem(text: String, selected: Boolean = false, onClick: () -> Unit = {}) {
    CompositionLocalProvider(LocalContentColor provides if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            WSpace()
            if (selected) Icon(imageVector = Icons.Filled.Check, contentDescription = null)
        }
    }
}

@Composable
private fun ProductCard(item: ProductWithImage?, placeholder: VectorPainter) {
    Card(
        modifier = Modifier
            .size(156.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                modifier = Modifier.size(64.dp),
                model = item?.imageUrlOrNull(),
                error = placeholder,
                contentDescription = item?.name,
                placeholder = placeholder
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item?.name ?: "",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1.0f))
            Text(
                text = "\$${item?.price ?: ""}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewThemes
@Composable
@Suppress("HardCodedStringLiteral")
private fun ProductsScreenContentPreview() {
    val products = (1L..15L).map {
        ProductWithImage(
            id = it.toString(),
            name = "$it ${generateAlphanumeric()}",
            price = BigDecimal("19.99"),
            description = "description$it",
            filename = null,
            tenant = null
        )
    }
    ErplyThemePreviewSurface {
        ProductsScreenContent(
            group = ProductGroup(
                id = "",
                parentId = "",
                order = 1,
                name = "Very long group name, da, da, dadadada",
                description = null,
                changed = 0
            ),
            pagingProducts = lazyPagingItems(products),
            sortingOrder = SortingOrder.PRICE_DESC
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewThemes
@Composable
private fun ProductsScreenContentEmptyLoadingPreview() {
    ErplyThemePreviewSurface {
        ProductsScreenContent(
            isLoading = true,
            pagingProducts = emptyLazyPagingItems(),
            sortingOrder = SortingOrder.PRICE_DESC
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewThemes
@Composable
private fun ProductsScreenContentEmptyPreview() {
    ErplyThemePreviewSurface {
        ProductsScreenContent(
            isLoading = false,
            pagingProducts = emptyLazyPagingItems(),
            sortingOrder = SortingOrder.PRICE_DESC
        )
    }
}

@Composable
private fun emptyLazyPagingItems() = flowOf(PagingData.empty<ProductWithImage>()).collectAsLazyPagingItems()

@Composable
private fun <T : Any> lazyPagingItems(items: List<T>): LazyPagingItems<T> = flowOf(PagingData.from(items)).collectAsLazyPagingItems()

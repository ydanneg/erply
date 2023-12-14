package com.ydanneg.erply.ui.screens.main.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductsRepository
import com.ydanneg.erply.data.repository.SortingOrder
import com.ydanneg.erply.model.ProductGroup
import com.ydanneg.erply.model.ProductWithImage
import com.ydanneg.erply.sync.WorkManagerSyncManager
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

data class UiState(
    val group: ProductGroup? = null,
    val isLoading: Boolean = false,
    val searchQuery: String? = null,
    val sortOder: SortingOrder = SortingOrder.PRICE_DESC
)


val UiState.notLoaded
    get() = group == null && !isLoading


// TODO: delegate to CdnApi
fun ProductWithImage.imageUrlOrNull(): String? = tenant?.let { tenant -> filename?.let { "https://cdn-sb.erply.com/images/$tenant/$it" } }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    productGroupsRepository: ProductGroupsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val productsRepository: ProductsRepository,
    private val workManagerSyncManager: WorkManagerSyncManager
) : ViewModel() {

    private val log = LoggerFactory.getLogger("ProductsScreenViewModel")

    private val groupId: String = checkNotNull(savedStateHandle[GROUP_ID_STATE_KEY])

    private val _sort = savedStateHandle.getStateFlow(SORT_ORDER_KEY, SortingOrder.PRICE_ASC)
    private val _searchQuery = savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null)

    val filteredProducts = _sort.flatMapLatest { sort ->
        _searchQuery.flatMapLatest { query ->
            if (query?.isNotBlank() == true && query.length > 1) {
                productsRepository.searchAllProducts(query.trim(), sort)
            } else {
                productsRepository.getAllProductsByGroupId(groupId, sort)
            }
        }
    }.cachedIn(viewModelScope)

    fun setSortingOder(sortOrder: SortingOrder) {
        savedStateHandle[SORT_ORDER_KEY] = sortOrder
    }

    val uiState = combine(
        productGroupsRepository.group(groupId).distinctUntilChanged(),
        workManagerSyncManager.isSyncing.distinctUntilChanged(),
        _searchQuery,
        _sort
    ) { group, isSyncing, searchQuery, sort ->
        UiState(
            group = group,
            isLoading = isSyncing,
            searchQuery = searchQuery,
            sortOder = sort
        )
    }.toStateFlow(
        viewModelScope, UiState(isLoading = true)
    )

    fun setSearchQuery(search: String?) {
        savedStateHandle[SEARCH_QUERY_KEY] = search
    }

    fun loadProducts() {
        log.debug("loadProducts...")//NON-NLS
        viewModelScope.launch {
            workManagerSyncManager.requestSync()
        }
    }

    companion object {
        private const val GROUP_ID_STATE_KEY = "groupId"
        private const val SEARCH_QUERY_KEY = "searchQuery"
        private const val SORT_ORDER_KEY = "sortOrder"

    }
}

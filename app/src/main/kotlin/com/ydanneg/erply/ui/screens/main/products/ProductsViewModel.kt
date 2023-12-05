package com.ydanneg.erply.ui.screens.main.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductsRepository
import com.ydanneg.erply.sync.WorkManagerSyncManager
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val GROUP_ID_STATE_KEY = "groupId"
const val SEARCH_QUERY_KEY = "searchQuery"

sealed class ProductsScreenUiState {
    data object Loading : ProductsScreenUiState()
    data object Success : ProductsScreenUiState()

    fun ProductsScreenUiState.isLoading() = this is Loading

}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    productGroupsRepository: ProductGroupsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val productsRepository: ProductsRepository,
    private val workManagerSyncManager: WorkManagerSyncManager
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle[GROUP_ID_STATE_KEY])

    val uiState = workManagerSyncManager.isSyncing
        .map { if (it) ProductsScreenUiState.Loading else ProductsScreenUiState.Success }
        .toStateFlow(viewModelScope, ProductsScreenUiState.Loading)
    val searchQuery = savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null)
    val group = productGroupsRepository.group(groupId).toStateFlow(viewModelScope, null)

    val filteredProducts = searchQuery.flatMapLatest { query ->
        if (query?.isNotBlank() == true) {
            productsRepository.productsByGroupIdAndNameLike(groupId, query)
        } else {
            productsRepository.productsByGroupId(groupId)
        }
    }.toStateFlow(viewModelScope, listOf())

    fun setSearchQuery(search: String?) {
        savedStateHandle[SEARCH_QUERY_KEY] = search
    }

    fun loadProducts() {
        viewModelScope.launch {
            workManagerSyncManager.requestSync()
        }
    }
}

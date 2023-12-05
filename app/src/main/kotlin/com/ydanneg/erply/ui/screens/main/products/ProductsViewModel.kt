package com.ydanneg.erply.ui.screens.main.products

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductsRepository
import com.ydanneg.erply.sync.WorkManagerSyncManager
import com.ydanneg.erply.util.LogUtils.TAG
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

const val GROUP_ID_STATE_KEY = "groupId"
const val SEARCH_QUERY_KEY = "searchQuery"

data class UiState(
    val group: ErplyProductGroup?,
    val isLoading: Boolean,
    val searchQuery: String?
)

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

    val uiState = combine(
        productGroupsRepository.group(groupId).distinctUntilChanged(),
        workManagerSyncManager.isSyncing.distinctUntilChanged(),
        savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null)
    ) { group, isSyncing, searchQuery ->
        UiState(
            group = group,
            isLoading = isSyncing,
            searchQuery = searchQuery
        )
    }.toStateFlow(viewModelScope, UiState(null, true, null))

    val filteredProducts = savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null)
        .flatMapLatest { query ->
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
        Log.d(TAG, "loadProducts...")
        viewModelScope.launch {
            workManagerSyncManager.requestSync()
        }
    }
}

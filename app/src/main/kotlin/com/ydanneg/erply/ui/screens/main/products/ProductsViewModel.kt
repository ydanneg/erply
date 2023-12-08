package com.ydanneg.erply.ui.screens.main.products

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductWithImagesRepository
import com.ydanneg.erply.model.ProductGroup
import com.ydanneg.erply.model.ProductWithImage
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

data class UiState(
    val group: ProductGroup? = null,
    val isLoading: Boolean = false,
    val searchQuery: String? = null
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
    private val productWithImagesRepository: ProductWithImagesRepository,
    private val workManagerSyncManager: WorkManagerSyncManager
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle[GROUP_ID_STATE_KEY])

    val filteredProducts = savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null)
        .flatMapLatest { query ->
            if (query?.isNotBlank() == true && query.length > 1) {
                productWithImagesRepository.searchAllProducts(query.trim())
            } else {
                productWithImagesRepository.getAllProductsByGroupId(groupId)
            }
        }.cachedIn(viewModelScope)


    val uiState = combine(
        productGroupsRepository.group(groupId).distinctUntilChanged(),
        workManagerSyncManager.isSyncing.distinctUntilChanged(),
        savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null),
    ) { group, isSyncing, searchQuery ->
        UiState(
            group = group,
            isLoading = isSyncing,
            searchQuery = searchQuery
        )
    }.toStateFlow(
        viewModelScope, UiState(isLoading = true)
    )

    fun setSearchQuery(search: String?) {
        savedStateHandle[SEARCH_QUERY_KEY] = search
    }

    fun loadProducts() {
        Log.d(TAG, "loadProducts...")//NON-NLS
        viewModelScope.launch {
            workManagerSyncManager.requestSync()
        }
    }

    companion object {
        private const val GROUP_ID_STATE_KEY = "groupId"
        private const val SEARCH_QUERY_KEY = "searchQuery"

    }
}

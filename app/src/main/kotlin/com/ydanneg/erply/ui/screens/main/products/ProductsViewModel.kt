package com.ydanneg.erply.ui.screens.main.products

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductWithImagesRepository
import com.ydanneg.erply.database.dao.ErplyProductWithImagesDao.ProductWithImage
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductPictureEntity
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
    val group: ErplyProductGroup? = null,
    val isLoading: Boolean = false,
    val searchQuery: String? = null
)

val UiState.notLoaded
    get() = group == null && !isLoading

data class ProductWithImages(
    val product: ErplyProduct,
    val images: List<ErplyProductPicture>
)

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
            if (query?.isNotBlank() == true) {
                productWithImagesRepository.searchProductsWithImagesPageable(groupId, query.trim())
            } else {
                productWithImagesRepository.productsWithImagesPageable(groupId)
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
        Log.d(TAG, "loadProducts...")
        viewModelScope.launch {
            workManagerSyncManager.requestSync()
        }
    }

    private fun Map<ProductEntity, List<ProductPictureEntity>>.fromEntity(): List<ProductWithImages> =
        map { item -> ProductWithImages(item.key.fromEntity(), item.value.map { it.fromEntity() }) }

    companion object {
        private const val GROUP_ID_STATE_KEY = "groupId"
        private const val SEARCH_QUERY_KEY = "searchQuery"

    }
}

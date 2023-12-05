package com.ydanneg.erply.ui.screens.main.products

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductWithImagesRepository
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
    val products: List<ProductWithImages> = listOf(),
    val isLoading: Boolean = false,
    val searchQuery: String? = null
)

data class ProductWithImages(
    val product: ErplyProduct,
    val images: List<ErplyProductPicture>
)

fun ProductWithImages.imageUrl(): String? = images.firstOrNull()?.let { "https://cdn-sb.erply.com/images/${it.tenant}/${it.filename}" }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    productGroupsRepository: ProductGroupsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val productWithImagesRepository: ProductWithImagesRepository,
    private val workManagerSyncManager: WorkManagerSyncManager
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle[GROUP_ID_STATE_KEY])

    private val filteredProducts = savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null)
        .flatMapLatest { query ->
            if (query?.isNotBlank() == true) {
                productWithImagesRepository.productsWithImagesByName(groupId, query)
            } else {
                productWithImagesRepository.productsWithImages(groupId)
            }
        }

    val uiState = combine(
        productGroupsRepository.group(groupId).distinctUntilChanged(),
        workManagerSyncManager.isSyncing.distinctUntilChanged(),
        savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null),
        filteredProducts
    ) { group, isSyncing, searchQuery, products ->
        UiState(
            group = group,
            products = products.fromEntity(),
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

package com.ydanneg.erply.ui.screens.main.products

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductsRepository
import com.ydanneg.erply.domain.GetProductsFromRemoteUseCase
import com.ydanneg.erply.util.LogUtils.TAG
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val GROUP_ID_STATE_KEY = "groupId"
const val SEARCH_QUERY_KEY = "searchQuery"

sealed class ProductsScreenUiState {
    data object Loading : ProductsScreenUiState()
    data object Success : ProductsScreenUiState()
    data class Error(val message: String = "An error occurred") : ProductsScreenUiState()

    fun ProductsScreenUiState.isLoading() = this is Loading
    fun ProductsScreenUiState.isError() = this is Error
}

@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    productGroupsRepository: ProductGroupsRepository,
    private val productsFromRemoteUseCase: GetProductsFromRemoteUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val productsRepository: ProductsRepository,
) : ViewModel() {
    private val groupId: String = checkNotNull(savedStateHandle[GROUP_ID_STATE_KEY])

    private var job: Job? = null

    private var _uiState = MutableStateFlow<ProductsScreenUiState>(ProductsScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val searchQuery = savedStateHandle.getStateFlow<String?>(SEARCH_QUERY_KEY, null)

    init {
        loadProducts()
    }

    fun setSearchQuery(search: String?) {
        Log.i(TAG, "setSearchQuery: $search")
        savedStateHandle[SEARCH_QUERY_KEY] = search
    }

    // TODO: combine to uiState
    val products = productsRepository.productsByGroupId(groupId).toStateFlow(viewModelScope, listOf())


    // TODO: combine to uiState
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredProducts = searchQuery.flatMapLatest { query ->
        if (query?.isNotBlank() == true) {
            productsRepository.productsByGroupIdAndNameLike(groupId, query)
        } else {
            productsRepository.productsByGroupId(groupId)
        }
    }.toStateFlow(viewModelScope, listOf())

    // TODO: combine to uiState
    val groupName = productGroupsRepository.group(groupId).map { it.name.en }.toStateFlow(viewModelScope, "")

    fun loadProducts() {
        if (job?.isActive == true) {
            Log.d(TAG, "loading is already in progress")
            return
        }

        Log.i(TAG, "loading products...")
        viewModelScope.launch {
            try {
                _uiState.value = ProductsScreenUiState.Loading
                productsFromRemoteUseCase(groupId)
                _uiState.value = ProductsScreenUiState.Success
            } catch (e: Throwable) {
                _uiState.value = ProductsScreenUiState.Error()
            }
        }
    }
}

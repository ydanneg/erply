package com.ydanneg.erply.ui.screens.main.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.util.LogUtils.TAG
import com.ydanneg.erply.util.toStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductGroupsScreenUiState {
    data object Loading : ProductGroupsScreenUiState()
    data object Success : ProductGroupsScreenUiState()
    data class Error(val message: String? = "An error occurred") : ProductGroupsScreenUiState()

    fun ProductGroupsScreenUiState.isLoading() = this is Loading
    fun ProductGroupsScreenUiState.isError() = this is Error
}

@HiltViewModel
class ProductGroupsScreenViewModel @Inject constructor(
    private val productGroupsRepository: ProductGroupsRepository
) : ViewModel() {
    private var job: Job? = null

    private var _uiState = MutableStateFlow<ProductGroupsScreenUiState>(ProductGroupsScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadProductGroups()
    }

    // TODO: combine to uiState
    val groups = productGroupsRepository.productGroups.toStateFlow(viewModelScope, listOf())

    fun loadProductGroups() {
        if (job?.isActive == true) {
            Log.d(TAG, "loading is already in progress")
            return
        }
        Log.d(TAG, "loading groups...")
        job = viewModelScope.launch {
            try {
                _uiState.value = ProductGroupsScreenUiState.Loading
                productGroupsRepository.loadProductGroups()
                _uiState.value = ProductGroupsScreenUiState.Success
            } catch (e: Throwable) {
                _uiState.value = ProductGroupsScreenUiState.Error(e.message)
            }
        }
    }
}
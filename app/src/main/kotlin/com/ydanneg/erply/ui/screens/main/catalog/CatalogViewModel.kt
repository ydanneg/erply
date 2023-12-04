package com.ydanneg.erply.ui.screens.main.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.domain.GetProductGroupsFromRemoteUseCase
import com.ydanneg.erply.sync.WorkManagerSyncManager
import com.ydanneg.erply.util.LogUtils.TAG
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
    productGroupsRepository: ProductGroupsRepository,
    private val productGroupsFromRemoteUseCase: GetProductGroupsFromRemoteUseCase,
    private val workManagerSyncManager: WorkManagerSyncManager
) : ViewModel() {
    private var job: Job? = null

//    private var _uiState = MutableStateFlow<ProductGroupsScreenUiState>(ProductGroupsScreenUiState.Loading)
//    val uiState = _uiState.asStateFlow()

    val uiState = workManagerSyncManager.isSyncing
        .map { if (it) ProductGroupsScreenUiState.Loading else ProductGroupsScreenUiState.Success }
        .toStateFlow(viewModelScope, ProductGroupsScreenUiState.Loading)

//    init {
//        loadProductGroups()
//    }

    // TODO: combine to uiState
    val groups = productGroupsRepository.productGroups.toStateFlow(viewModelScope, listOf())

    fun loadProductGroups() {
        workManagerSyncManager.requestSync()
//        if (job?.isActive == true) {
//            Log.d(TAG, "loading is already in progress")
//            return
//        }
//        Log.d(TAG, "loading groups...")
//        job = viewModelScope.launch {
//            try {
////                _uiState.value = ProductGroupsScreenUiState.Loading
//                productGroupsFromRemoteUseCase()
////                _uiState.value = ProductGroupsScreenUiState.Success
//            } catch (e: Throwable) {
//                Log.e(TAG, "error", e)
//                _uiState.value = ProductGroupsScreenUiState.Error(e.message)
//            }
//        }
    }
}
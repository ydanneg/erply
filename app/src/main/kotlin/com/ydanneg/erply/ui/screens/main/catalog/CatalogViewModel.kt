package com.ydanneg.erply.ui.screens.main.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.sync.WorkManagerSyncManager
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductGroupsScreenUiState {
    data object Loading : ProductGroupsScreenUiState()
    data object Success : ProductGroupsScreenUiState()

    fun ProductGroupsScreenUiState.isLoading() = this is Loading
}

@HiltViewModel
class ProductGroupsScreenViewModel @Inject constructor(
    productGroupsRepository: ProductGroupsRepository,
    private val workManagerSyncManager: WorkManagerSyncManager
) : ViewModel() {

    val uiState = workManagerSyncManager.isSyncing
        .map { if (it) ProductGroupsScreenUiState.Loading else ProductGroupsScreenUiState.Success }
        .toStateFlow(viewModelScope, ProductGroupsScreenUiState.Loading)

    val groups = productGroupsRepository.productGroups.toStateFlow(viewModelScope, listOf())

    fun loadProductGroups() {
        viewModelScope.launch {
            workManagerSyncManager.requestSync()
        }
    }
}

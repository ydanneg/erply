package com.ydanneg.erply.ui.screens.main.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.model.ProductGroup
import com.ydanneg.erply.sync.WorkManagerSyncManager
import com.ydanneg.erply.util.LogUtils.TAG
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val groups: List<ProductGroup> = listOf(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ProductGroupsScreenViewModel @Inject constructor(
    productGroupsRepository: ProductGroupsRepository,
    private val workManagerSyncManager: WorkManagerSyncManager
) : ViewModel() {

    val uiState = combine(
        productGroupsRepository.productGroups,
        workManagerSyncManager.isSyncing.distinctUntilChanged()
    ) { groups, isSyncing ->
        UiState(
            groups = groups,
            isLoading = isSyncing
        )
    }.toStateFlow(viewModelScope, UiState(isLoading = true))

    fun loadProductGroups() {
        Log.d(TAG, "loadProducts...")//NON-NLS
        viewModelScope.launch {
            workManagerSyncManager.requestSync()
        }
    }
}

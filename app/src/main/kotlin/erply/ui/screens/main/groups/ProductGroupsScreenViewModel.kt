package erply.ui.screens.main.groups

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.ProductGroupsRepository
import erply.data.repository.UserSessionRepository
import erply.util.LogUtils.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductGroupsScreenUiState {
    data object Loading : ProductGroupsScreenUiState()
    data object Success : ProductGroupsScreenUiState()
    data class Error(val message: String?) : ProductGroupsScreenUiState()

    fun ProductGroupsScreenUiState.isLoading() = this is Loading
    fun ProductGroupsScreenUiState.isError() = this is Error
}

@HiltViewModel
class ProductGroupsScreenViewModel @Inject constructor(
    private val productGroupsRepository: ProductGroupsRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {
    private var job: Job? = null

    private var _uiState = MutableStateFlow<ProductGroupsScreenUiState>(ProductGroupsScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    val groups = productGroupsRepository.productGroups
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            listOf()
        )

    fun loadGroups() {
        if (job?.isActive == true) {
            Log.d(TAG, "loading is already in progress")
            return
        }
        Log.d(TAG, "loading groups...")
        job = viewModelScope.launch {
            try {
                _uiState.value = ProductGroupsScreenUiState.Loading
                productGroupsRepository.loadProductGroups()
                delay(1000)
                _uiState.value = ProductGroupsScreenUiState.Success
            } catch (e: Throwable) {
                _uiState.value = ProductGroupsScreenUiState.Error(e.message)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            userSessionRepository.logout()
        }
    }
}
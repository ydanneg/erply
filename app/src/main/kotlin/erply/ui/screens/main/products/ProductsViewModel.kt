package erply.ui.screens.main.products

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.ProductGroupsRepository
import erply.data.repository.ProductsRepository
import erply.data.repository.UserSessionRepository
import erply.util.LogUtils.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val GROUP_ID_STATE_KEY = "groupId"

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
    savedStateHandle: SavedStateHandle,
    private val productsRepository: ProductsRepository,
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {
    private val groupId: String = checkNotNull(savedStateHandle[GROUP_ID_STATE_KEY])

    private var job: Job? = null

    private var _uiState = MutableStateFlow<ProductsScreenUiState>(ProductsScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    val products = productsRepository.productsByGroupId(groupId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            listOf()
        )

    val groupName = productGroupsRepository.group(groupId).map { it.name.en }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ""
        )

    fun loadProducts() {
        if (job?.isActive == true) {
            Log.d(TAG, "loading is already in progress")
            return
        }

        Log.i(TAG, "loading products...")
        viewModelScope.launch {
            try {
                _uiState.value = ProductsScreenUiState.Loading
                productsRepository.loadProductsByGroupId(groupId)
                _uiState.value = ProductsScreenUiState.Success
            } catch (e: Throwable) {
                _uiState.value = ProductsScreenUiState.Error()
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            userSessionRepository.logout()
        }
    }
}
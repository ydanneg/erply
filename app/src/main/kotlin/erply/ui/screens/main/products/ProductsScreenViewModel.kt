package erply.ui.screens.main.products

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.ProductsRepository
import erply.data.repository.UserSessionRepository
import erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val GROUP_ID_STATE_KEY = "groupId"

@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val groupId: String = checkNotNull(savedStateHandle[GROUP_ID_STATE_KEY])

    init {
        loadProducts()
    }

    val products = productsRepository.productsByGroupId(groupId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            listOf()
        )

    private fun loadProducts() {
        Log.i(TAG, "loading products...")
        viewModelScope.launch {
            productsRepository.loadProductsByGroupId(groupId)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            userSessionRepository.logout()
        }
    }
}
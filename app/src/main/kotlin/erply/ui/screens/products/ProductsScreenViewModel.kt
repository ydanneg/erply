package erply.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.ProductRepository
import erply.data.repository.UserSessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    init {
        loadProducts()
    }

    val products = productRepository.products

    private var job: Job? = null

    fun loadProducts() {
        if (job?.isActive == true) {
            return
        }
        job = viewModelScope.launch {
            productRepository.loadProducts()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            userSessionRepository.logout()
        }
    }
}
package erply.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.ProductsRepository
import erply.data.repository.UserSessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    val products = productsRepository.products

    private var job: Job? = null

    fun loadProducts(groupId: String? = null) {
        if (job?.isActive == true) {
            return
        }
        job = viewModelScope.launch {
            productsRepository.loadProducts(groupId)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            userSessionRepository.logout()
        }
    }
}
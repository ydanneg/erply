package erply.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.ProductGroupsRepository
import erply.data.repository.UserSessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductGroupsScreenViewModel @Inject constructor(
    private val productGroupsRepository: ProductGroupsRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    init {
        loadGroups()
    }

    val groups = productGroupsRepository.productGroups

    private var job: Job? = null

    fun loadGroups() {
        if (job?.isActive == true) {
            return
        }
        job = viewModelScope.launch {
            productGroupsRepository.loadProductGroups()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            userSessionRepository.logout()
        }
    }
}
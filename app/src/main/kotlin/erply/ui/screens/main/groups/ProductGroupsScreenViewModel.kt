package erply.ui.screens.main.groups

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.ProductGroupsRepository
import erply.data.repository.UserSessionRepository
import erply.util.LogUtils.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductGroupsScreenViewModel @Inject constructor(
    private val productGroupsRepository: ProductGroupsRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    val groups = productGroupsRepository.productGroups
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            listOf()
        )

    private var job: Job? = null

    fun loadGroups() {
        Log.i(TAG, "loading groups...")
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
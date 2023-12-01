package erply.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ErplyAppViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    val username = userSessionRepository.userSessionData.map { it.username }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Anonymous")

    val isLoggedIn = userSessionRepository.isLoggedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false
    )

    fun logOut() {
        viewModelScope.launch {
            userSessionRepository.logout()
        }
    }
}

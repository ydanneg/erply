package erply.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.UserSessionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(val userSessionRepository: UserSessionRepository) : ViewModel() {

    fun logOut() = viewModelScope.launch {
        userSessionRepository.logout()
    }
}
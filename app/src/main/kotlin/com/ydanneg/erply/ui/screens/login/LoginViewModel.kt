package com.ydanneg.erply.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.data.repository.UserSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

sealed class LoginUIState {
    data object Idle : LoginUIState()
    data object Loading : LoginUIState()
    data object LoggedIn : LoginUIState()

    data class Error(val error: ErplyApiError) : LoginUIState()
}

fun LoginUIState.isLoading() = this is LoginUIState.Loading
fun LoginUIState.getError() = if (this is LoginUIState.Error) error else null


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val log = LoggerFactory.getLogger(LoginScreenViewModel::class.java)

    private var loginJob: Job? = null

    private var _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Idle)
    val uiState = _uiState.asStateFlow()

    fun doLogin(clientId: String, username: String, password: String) {
        log.debug("doLogin: $clientId, $username")
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            try {
                _uiState.value = LoginUIState.Loading
                userSessionRepository.login(clientId, username, password)
                _uiState.value = LoginUIState.LoggedIn
            } catch (e: ErplyApiException) {
                log.error("error", e)//NON-NLS
                _uiState.value = LoginUIState.Error(e.type)
            }
        }
    }

}

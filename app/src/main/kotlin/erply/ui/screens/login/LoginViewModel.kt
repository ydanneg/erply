package erply.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.api.ErplyApiError
import com.ydanneg.erply.api.ErplyApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.UserSessionRepository
import erply.util.LogUtils.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUIState {
    data object Idle : LoginUIState()
    data object Loading : LoginUIState()
    data object Success : LoginUIState()

    data class Error(val message: String?) : LoginUIState()
}

fun LoginUIState.isLoading() = this is LoginUIState.Loading
fun LoginUIState.getError() = if (this is LoginUIState.Error) message else null


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private var loginJob: Job? = null

    fun doLogin(clientId: String, username: String, password: String) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            try {
                _uiState.value = LoginUIState.Loading
                userSessionRepository.login(clientId, username, password)
                _uiState.value = LoginUIState.Success
            } catch (e: ErplyApiException) {
                Log.e(TAG, "error", e)
                _uiState.value = LoginUIState.Error(e.type.message())
            }
        }
    }

    private fun ErplyApiError.message(): String = when (this) {
        ErplyApiError.ConnectionError -> "Connection error"
        ErplyApiError.WrongCredentials -> "Wrong credentials"
        ErplyApiError.SessionExpired -> "Session expired"
        ErplyApiError.RequestLimitReached -> "Request limit reached"
        ErplyApiError.AccountNotFound -> "Account not found"
        ErplyApiError.Unknown -> "Unknown error. Try again later."
    }
}
package erply.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.repository.UserDataRepository
import erply.model.UserData
import erply.model.UserPreferences
import erply.util.toStateFlow
import javax.inject.Inject

@HiltViewModel
class ErplyAppViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    val userData = userDataRepository.userData.toStateFlow(viewModelScope, UserData(null, UserPreferences()))
}

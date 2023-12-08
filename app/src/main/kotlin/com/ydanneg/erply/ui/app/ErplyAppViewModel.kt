package com.ydanneg.erply.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ydanneg.erply.data.repository.UserDataRepository
import com.ydanneg.erply.model.UserData
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.util.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ErplyAppViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    val userData = userDataRepository.userData.toStateFlow(viewModelScope, UserData(null, UserPreferences()))
}

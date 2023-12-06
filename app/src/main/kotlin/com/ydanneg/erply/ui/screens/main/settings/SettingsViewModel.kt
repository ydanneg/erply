package com.ydanneg.erply.ui.screens.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.util.toStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : ViewModel() {

    // TODO: combine to uiState
    val userPreferences = userPreferencesDataSource.userPreferences.toStateFlow(viewModelScope, UserPreferences())

    fun changeDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
        }
    }
}

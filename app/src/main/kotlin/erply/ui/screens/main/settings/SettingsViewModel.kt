package erply.ui.screens.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.datastore.UserPreferencesDataSource
import erply.model.DarkThemeConfig
import erply.model.UserPreferences
import erply.util.toStateFlow
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
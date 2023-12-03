package erply.ui.screens.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.datastore.UserPreferencesDataSource
import erply.model.DarkThemeConfig
import erply.model.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : ViewModel() {

    val userPreferences = userPreferencesDataSource.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())

    fun changeDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
        }
    }
}
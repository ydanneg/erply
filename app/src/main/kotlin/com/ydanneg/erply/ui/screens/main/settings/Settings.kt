package com.ydanneg.erply.ui.screens.main.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.ui.components.ErplyDrawerTopAppbar
import com.ydanneg.erply.ui.screens.main.MainScreenState
import com.ydanneg.erply.ui.screens.main.rememberMainScreenState
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes


@Composable
@PreviewThemes
private fun SettingsScreenPreview() {
    ErplyThemePreviewSurface {
        SettingsScreenContent(userPreferences = UserPreferences(DarkThemeConfig.DARK))
    }
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    mainScreenState: MainScreenState
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()

    SettingsScreenContent(mainScreenState, userPreferences, onDarkThemeConfigChange = viewModel::changeDarkThemeConfig)
}


@Composable
private fun SettingsScreenContent(
    mainScreenState: MainScreenState = rememberMainScreenState(),
    userPreferences: UserPreferences,
    onDarkThemeConfigChange: (DarkThemeConfig) -> Unit = {}
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ErplyDrawerTopAppbar(
                title = "Settings",
                drawerState = mainScreenState.drawerState
            )
        },
        content = { paddingValues ->
            Column(Modifier.padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally) {
                SettingsSectionTitle("Select theme")
                Column(Modifier.selectableGroup()) {
                    SettingsThemeChooserRow(
                        text = "System default",
                        selected = userPreferences.darkThemeConfig == DarkThemeConfig.FOLLOW_SYSTEM,
                        onClick = { onDarkThemeConfigChange(DarkThemeConfig.FOLLOW_SYSTEM) }
                    )
                    SettingsThemeChooserRow(
                        text = "Dark",
                        selected = userPreferences.darkThemeConfig == DarkThemeConfig.DARK,
                        onClick = { onDarkThemeConfigChange(DarkThemeConfig.DARK) }
                    )
                    SettingsThemeChooserRow(
                        text = "Light",
                        selected = userPreferences.darkThemeConfig == DarkThemeConfig.LIGHT,
                        onClick = { onDarkThemeConfigChange(DarkThemeConfig.LIGHT) }
                    )
                }
            }
        }
    )
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun SettingsThemeChooserRow(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

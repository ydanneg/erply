package com.ydanneg.erply.ui.theme

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ErplyThemePreviewSurface(
    content: @Composable () -> Unit
) {
    ErplyTheme {
        Surface(content = content)
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
annotation class PreviewThemes
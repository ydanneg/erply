package erply.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun FadedProgressIndicator(visible: Boolean = true) {

    FadedVisibility(visible) {
        if (LocalInspectionMode.current) {
            // show 75% in previews
            CircularProgressIndicator(progress = { 0.75f })
        } else {
            CircularProgressIndicator()
        }
    }
}
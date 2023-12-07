package com.ydanneg.erply.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FadedProgressIndicator(modifier: Modifier = Modifier, visible: Boolean = true) {
    FadedVisibility(modifier, visible) {
        if (LocalInspectionMode.current) {
            // show 75% in previews
            CircularProgressIndicator(progress = { 0.75f })
        } else {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun FadedLinerProgressIndicator(height: Dp = 4.dp, visible: Boolean = true) {
    FadedVisibility(visible = visible) {
        if (LocalInspectionMode.current) {
            // show 75% in previews
            LinearProgressIndicator(modifier = Modifier
                .height(height)
                .fillMaxWidth(), progress = { 0.75f })
        } else {
            LinearProgressIndicator(modifier = Modifier
                .height(height)
                .fillMaxWidth())
        }
    }
}

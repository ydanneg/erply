package com.ydanneg.erply.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes

@Composable
@PreviewThemes
private fun LoadingPreview() {
    ErplyThemePreviewSurface {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Loading()
        }
    }
}

@Composable
fun Loading() {
    Column(modifier = Modifier.wrapContentSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Loading...", style = MaterialTheme.typography.headlineSmall)
//        VSpace(16.dp)
//        Icon(imageVector = Icons.Filled.Sync, contentDescription = null)
    }
}

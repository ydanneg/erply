package com.ydanneg.erply.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwipeDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ydanneg.erply.R
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface

@Composable
@Preview(device = Devices.NEXUS_7, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun NothingToShowPreview() {
    ErplyThemePreviewSurface {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            NothingToShow()
        }
    }
}

@Composable
fun NothingToShow() {
    Column(modifier = Modifier.wrapContentSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.nothing_to_show_title), style = MaterialTheme.typography.headlineSmall)
        VSpace(8.dp)
        Text(text = stringResource(R.string.nothing_to_show_subtitle))
        VSpace(16.dp)
        Icon(imageVector = Icons.Filled.SwipeDown, contentDescription = null)
    }
}

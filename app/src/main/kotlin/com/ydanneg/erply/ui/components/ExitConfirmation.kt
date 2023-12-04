package com.ydanneg.erply.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ydanneg.erply.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExitConfirmation() {
    var exitOnBack by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val exitMessage = stringResource(R.string.exit_confirmation)

    BackHandler(enabled = !exitOnBack) {
        exitOnBack = true
        scope.launch {
            Toast.makeText(context, exitMessage, Toast.LENGTH_SHORT).show()
            delay(2000)
            exitOnBack = false
        }
    }
}
package com.ydanneg.erply.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun VSpace(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun HSpace(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun ColumnScope.WSpace(weight: Float = 1.0f) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun RowScope.WSpace() {
    Spacer(modifier = Modifier.weight(1.0f))
}


package com.ydanneg.erply.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VSpace(height: Dp = 0.dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun HSpace(width: Dp = 0.dp) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun ColumnScope.WSpace(weight: Float = 1.0f) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun RowScope.WSpace(weight: Float = 1.0f) {
    Spacer(modifier = Modifier.weight(weight))
}

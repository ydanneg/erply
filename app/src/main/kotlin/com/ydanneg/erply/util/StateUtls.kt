package com.ydanneg.erply.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.toStateFlow(
    scope: CoroutineScope,
    default: T,
    started: SharingStarted = SharingStarted.WhileSubscribed(5_000)
) = stateIn(
    scope = scope,
    started = started,
    initialValue = default
)
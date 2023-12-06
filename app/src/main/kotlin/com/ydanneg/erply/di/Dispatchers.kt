package com.ydanneg.erply.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatcher: ErplyDispatchers)

enum class ErplyDispatchers {
    Default, IO,
}

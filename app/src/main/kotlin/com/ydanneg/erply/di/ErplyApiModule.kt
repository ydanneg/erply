package com.ydanneg.erply.di

import android.os.Build
import android.util.Log
import com.ydanneg.erply.BuildConfig
import com.ydanneg.erply.api.client.ErplyApiClient
import com.ydanneg.erply.api.client.ErplyApiClientConfiguration
import com.ydanneg.erply.api.client.ErplyApiClientLogLevel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ErplyApiModule {

    @Provides
    @Singleton
    fun providesErplyApi(): ErplyApiClient =
        ErplyApiClient(
            ErplyApiClientConfiguration(
                logger = { Log.v("HTTP", it) },
                logLevel = valueOrNull<ErplyApiClientLogLevel>(BuildConfig.CLIENT_LOG_LEVEL) ?: ErplyApiClientLogLevel.NONE,
                baseUrl = BuildConfig.CLIENT_PIM_BASE_URL,
                userAgent = "${BuildConfig.CLIENT_USER_AGENT}/${BuildConfig.VERSION_NAME}.{${BuildConfig.VERSION_CODE}} (Android SDK ${Build.VERSION.SDK_INT}; ${Build.DEVICE})",
                connectionTimeoutSeconds = BuildConfig.CLIENT_CONNECT_TIMEOUT_SECONDS.toLong(),
                readTimeoutSeconds = BuildConfig.CLIENT_READ_TIMEOUT_SECONDS.toLong(),
                writeTimeoutSeconds = BuildConfig.CLIENT_WRITE_TIMEOUT_SECONDS.toLong()
            )
        )
}

inline fun <reified T : Enum<T>> valueOrNull(name: String): T? = enumValues<T>().find { it.name.equals(name, ignoreCase = true) }

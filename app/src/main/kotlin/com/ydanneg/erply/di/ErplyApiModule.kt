package com.ydanneg.erply.di

import android.util.Log
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
                logLevel = ErplyApiClientLogLevel.INFO
            )
        )
}

package com.ydanneg.erply.di

import android.util.Log
import com.ydanneg.erply.api.client.ErplyApiClient
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
    fun providesErplyApi(): ErplyApiClient = ErplyApiClient(onLog = {
        Log.v("HTTP", it)
    })
}

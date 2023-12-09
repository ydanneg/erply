package com.ydanneg.erply.di

import com.ydanneg.erply.security.AndroidEncryptionManager
import com.ydanneg.erply.security.EncryptionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface EncryptionModule {

    @Singleton
    @Binds
    fun bindAndroidEncryptionManager(impl: AndroidEncryptionManager): EncryptionManager
}

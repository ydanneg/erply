package com.ydanneg.erply.di

import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductGroupsRepositoryImpl
import com.ydanneg.erply.data.repository.ProductRepository
import com.ydanneg.erply.data.repository.ProductRepositoryImpl
import com.ydanneg.erply.data.repository.UserDataRepository
import com.ydanneg.erply.data.repository.UserDataRepositoryImpl
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.data.repository.UserSessionRepositoryImpl
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.datastore.UserPreferencesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindProductGroupRepository(impl: ProductGroupsRepositoryImpl): ProductGroupsRepository

    @Singleton
    @Binds
    fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Singleton
    @Binds
    fun bindUserSessionRepository(impl: UserSessionRepositoryImpl): UserSessionRepository

    @Singleton
    @Binds
    fun bindUserDataRepository(impl: UserDataRepositoryImpl): UserDataRepository

    @Singleton
    @Binds
    fun bindUSerPreferences(impl: UserPreferencesDataSourceImpl): UserPreferencesDataSource
}

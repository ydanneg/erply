package com.ydanneg.erply.di

import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductGroupsRepositoryImpl
import com.ydanneg.erply.data.repository.ProductWithImagesRepository
import com.ydanneg.erply.data.repository.ProductWithImagesRepositoryImpl
import com.ydanneg.erply.data.repository.ProductsRepository
import com.ydanneg.erply.data.repository.ProductsRepositoryImpl
import com.ydanneg.erply.data.repository.UserDataRepository
import com.ydanneg.erply.data.repository.UserDataRepositoryImpl
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.data.repository.UserSessionRepositoryImpl
import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.dao.ErplyProductWithImageDao
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providesProductWithImagesRepository(
        erplyProductWithImageDao: ErplyProductWithImageDao,
        userSessionRepository: UserSessionRepository
    ): ProductWithImagesRepository = ProductWithImagesRepositoryImpl(erplyProductWithImageDao, userSessionRepository)

    @Provides
    @Singleton
    fun providesProductGroupRepository(
        erplyProductGroupDao: ErplyProductGroupDao,
        userSessionRepository: UserSessionRepository
    ): ProductGroupsRepository = ProductGroupsRepositoryImpl(erplyProductGroupDao, userSessionRepository)

    @Provides
    @Singleton
    fun providesProductsRepository(
        erplyProductDao: ErplyProductDao,
        userSessionRepository: UserSessionRepository
    ): ProductsRepository = ProductsRepositoryImpl(erplyProductDao, userSessionRepository)

    @Provides
    @Singleton
    fun providesUserSessionRepository(
        erplyNetworkDataSource: ErplyNetworkDataSource,
        userSessionDataSource: UserSessionDataSource
    ): UserSessionRepository = UserSessionRepositoryImpl(erplyNetworkDataSource, userSessionDataSource)

    @Provides
    @Singleton
    fun providesUserDataRepository(
        userSessionRepository: UserSessionRepository,
        userPreferencesDataSource: UserPreferencesDataSource
    ): UserDataRepository = UserDataRepositoryImpl(userSessionRepository, userPreferencesDataSource)
}

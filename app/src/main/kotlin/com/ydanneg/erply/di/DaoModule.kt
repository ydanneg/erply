package com.ydanneg.erply.di

import com.ydanneg.erply.database.ErplyDatabase
import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.dao.ErplyProductImageDao
import com.ydanneg.erply.database.dao.ErplyProductWithImagesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesProductsDao(
        database: ErplyDatabase,
    ): ErplyProductDao = database.productDao()

    @Provides
    fun providesGroupsDao(
        database: ErplyDatabase,
    ): ErplyProductGroupDao = database.groupDao()

    @Provides
    fun providesImageDao(
        database: ErplyDatabase,
    ): ErplyProductImageDao = database.imageDao()

    @Provides
    fun providesProductWithImagesDao(
        database: ErplyDatabase,
    ): ErplyProductWithImagesDao = database.productWithImagesDao()
}

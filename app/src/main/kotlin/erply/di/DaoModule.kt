package erply.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import erply.database.ErplyDatabase
import erply.database.dao.ErplyProductGroupDao
import erply.database.dao.ErplyProductDao

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
}

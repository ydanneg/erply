package erply.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import erply.database.ErplyDatabase
import erply.database.dao.GroupsDao
import erply.database.dao.ProductsDao

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesProductsDao(
        database: ErplyDatabase,
    ): ProductsDao = database.productDao()

    @Provides
    fun providesGroupsDao(
        database: ErplyDatabase,
    ): GroupsDao = database.groupDao()
}

package com.ydanneg.erply.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.dao.ErplyProductImageDao
import com.ydanneg.erply.database.dao.ErplyProductWithImagesDao
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.database.model.ProductPictureEntity
import com.ydanneg.erply.database.util.InstantConverter

@Database(
    entities = [
        ProductEntity::class,
        ProductGroupEntity::class,
        ProductPictureEntity::class
    ],
    version = 1,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
abstract class ErplyDatabase : RoomDatabase() {
    abstract fun productDao(): ErplyProductDao
    abstract fun groupDao(): ErplyProductGroupDao
    abstract fun imageDao(): ErplyProductImageDao
    abstract fun productWithImagesDao(): ErplyProductWithImagesDao
}

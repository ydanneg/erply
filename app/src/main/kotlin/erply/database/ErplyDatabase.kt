package erply.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import erply.database.dao.ErplyProductGroupDao
import erply.database.dao.ErplyProductDao
import erply.database.model.ProductGroupEntity
import erply.database.model.ProductEntity
import erply.database.util.InstantConverter

@Database(
    entities = [
        ProductEntity::class,
        ProductGroupEntity::class
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
}
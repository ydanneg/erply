package erply.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import erply.database.dao.GroupsDao
import erply.database.dao.ProductsDao
import erply.database.model.GroupEntity
import erply.database.model.ProductEntity
import erply.database.util.InstantConverter

@Database(
    entities = [
        ProductEntity::class,
        GroupEntity::class
    ],
    version = 1,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
abstract class ErplyDatabase : RoomDatabase() {
    abstract fun productDao(): ProductsDao
    abstract fun groupDao(): GroupsDao
}
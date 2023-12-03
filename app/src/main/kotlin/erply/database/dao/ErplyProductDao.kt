package erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import erply.database.model.PRODUCTS_TABLE_NAME
import erply.database.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErplyProductDao {

    @Query(value = "SELECT * FROM $PRODUCTS_TABLE_NAME")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM $PRODUCTS_TABLE_NAME WHERE id = :productId")
    fun getById(productId: String): Flow<ProductEntity>

    @Query("SELECT * FROM $PRODUCTS_TABLE_NAME WHERE groupId = :groupId")
    fun getAllByGroupId(groupId: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(products: List<ProductEntity>): List<Long>

    @Upsert
    suspend fun upsert(entities: List<ProductEntity>)

    @Query("DELETE FROM $PRODUCTS_TABLE_NAME WHERE id in (:ids)")
    suspend fun delete(ids: List<String>)
}
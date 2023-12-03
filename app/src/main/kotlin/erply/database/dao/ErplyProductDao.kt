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

    @Query(value = "SELECT * FROM $PRODUCTS_TABLE_NAME WHERE clientCode = :clientCode")
    fun getAll(clientCode: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM $PRODUCTS_TABLE_NAME WHERE id = :productId AND clientCode = :clientCode")
    fun getById(clientCode: String, productId: String): Flow<ProductEntity>

    @Query("SELECT * FROM $PRODUCTS_TABLE_NAME WHERE groupId = :groupId AND clientCode = :clientCode")
    fun getAllByGroupId(clientCode: String, groupId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM $PRODUCTS_TABLE_NAME WHERE groupId = :groupId AND clientCode = :clientCode AND name LIKE '%' || :name || '%'")
    fun findAllByGroupIdAndName(clientCode: String, groupId: String, name: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(products: List<ProductEntity>): List<Long>

    @Upsert
    suspend fun upsert(entities: List<ProductEntity>)

    @Query("DELETE FROM $PRODUCTS_TABLE_NAME WHERE id in (:ids) AND clientCode = :clientCode")
    suspend fun delete(clientCode: String, ids: List<String>)
}
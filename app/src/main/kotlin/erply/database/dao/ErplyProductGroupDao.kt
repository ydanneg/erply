package erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import erply.database.model.GROUPS_TABLE_NAME
import erply.database.model.ProductGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErplyProductGroupDao {

    @Query(value = "SELECT * FROM $GROUPS_TABLE_NAME")
    fun getAll(): Flow<List<ProductGroupEntity>>

    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE id = :productId")
    fun getById(productId: String): Flow<ProductGroupEntity>

    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE parentId = :parentId")
    fun getAllByParentId(parentId: String): Flow<List<ProductGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(products: List<ProductGroupEntity>): List<Long>

    @Upsert
    suspend fun upsert(entities: List<ProductGroupEntity>)

    @Query("DELETE FROM $GROUPS_TABLE_NAME WHERE id in (:ids)")
    suspend fun delete(ids: List<String>)
}
package erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import erply.database.model.GROUPS_TABLE_NAME
import erply.database.model.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupsDao {

    @Query(value = "SELECT * FROM $GROUPS_TABLE_NAME")
    fun getAll(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE id = :productId")
    fun getById(productId: String): Flow<GroupEntity>

    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE parentId = :parentId")
    fun getAllByParentId(parentId: String): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(products: List<GroupEntity>): List<Long>

    @Upsert
    suspend fun upsert(entities: List<GroupEntity>)

    @Query("DELETE FROM $GROUPS_TABLE_NAME WHERE id in (:ids)")
    suspend fun delete(ids: List<String>)
}
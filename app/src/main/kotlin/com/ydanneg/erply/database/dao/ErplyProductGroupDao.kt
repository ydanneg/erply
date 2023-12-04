package com.ydanneg.erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ydanneg.erply.database.model.GROUPS_TABLE_NAME
import com.ydanneg.erply.database.model.ProductGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErplyProductGroupDao {

    @Query(value = "SELECT * FROM $GROUPS_TABLE_NAME WHERE clientCode = :clientCode")
    fun getAll(clientCode: String): Flow<List<ProductGroupEntity>>

    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE id = :productId AND clientCode = :clientCode")
    fun getById(clientCode: String, productId: String): Flow<ProductGroupEntity>

    @Query("SELECT * FROM $GROUPS_TABLE_NAME WHERE parentId = :parentId AND clientCode = :clientCode")
    fun getAllByParentId(clientCode: String, parentId: String): Flow<List<ProductGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(products: List<ProductGroupEntity>): List<Long>

    @Upsert
    suspend fun upsert(entities: List<ProductGroupEntity>)

    @Query("DELETE FROM $GROUPS_TABLE_NAME WHERE id in (:ids) AND clientCode = :clientCode")
    suspend fun delete(clientCode: String, ids: List<String>)
}
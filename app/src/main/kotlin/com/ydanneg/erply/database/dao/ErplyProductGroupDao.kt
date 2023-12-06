package com.ydanneg.erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ydanneg.erply.database.model.PRODUCT_GROUPS_TABLE_NAME
import com.ydanneg.erply.database.model.ProductGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErplyProductGroupDao {

    @Query(
        """
        SELECT * FROM $PRODUCT_GROUPS_TABLE_NAME 
        WHERE clientCode = :clientCode
        ORDER BY `order` ASC
        """
    )
    fun getAll(clientCode: String): Flow<List<ProductGroupEntity>>

    @Query(
        """
        SELECT * FROM $PRODUCT_GROUPS_TABLE_NAME 
        WHERE clientCode = :clientCode 
            AND id = :productId
        """
    )
    fun getById(clientCode: String, productId: String): Flow<ProductGroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entities: List<ProductGroupEntity>)

    @Upsert
    suspend fun upsert(entities: List<ProductGroupEntity>)

    @Query(
        """
        DELETE FROM $PRODUCT_GROUPS_TABLE_NAME 
        WHERE clientCode = :clientCode
            AND id in (:ids) 
        """
    )
    suspend fun delete(clientCode: String, ids: List<String>)
}

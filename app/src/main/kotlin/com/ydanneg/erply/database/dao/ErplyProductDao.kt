package com.ydanneg.erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ydanneg.erply.database.model.PRODUCTS_TABLE_NAME
import com.ydanneg.erply.database.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErplyProductDao {

    @Query(
        """
        SELECT * FROM $PRODUCTS_TABLE_NAME 
        WHERE clientCode = :clientCode
        """
    )
    fun getAll(clientCode: String): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT * FROM $PRODUCTS_TABLE_NAME 
        WHERE clientCode = :clientCode
            AND id = :productId 
        """
    )
    fun getById(clientCode: String, productId: String): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entities: List<ProductEntity>)

    @Upsert
    suspend fun upsert(entities: List<ProductEntity>)

    @Query(
        """
        DELETE FROM $PRODUCTS_TABLE_NAME 
        WHERE clientCode = :clientCode
            AND id in (:ids) 
        """
    )
    suspend fun delete(clientCode: String, ids: List<String>)
}

package com.ydanneg.erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ydanneg.erply.database.model.PRODUCT_IMAGES_TABLE_NAME
import com.ydanneg.erply.database.model.ProductPictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErplyProductImageDao {

    @Query(
        """
        SELECT * FROM $PRODUCT_IMAGES_TABLE_NAME 
        WHERE clientCode = :clientCode
            AND productId = :productId 
        """
    )
    fun findAllByProductId(clientCode: String, productId: String): Flow<List<ProductPictureEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entities: List<ProductPictureEntity>)

    @Upsert
    suspend fun upsert(entities: List<ProductPictureEntity>)

    @Query(
        """
        DELETE FROM $PRODUCT_IMAGES_TABLE_NAME 
        WHERE clientCode = :clientCode
            AND id in (:ids) 
        """
    )
    suspend fun delete(clientCode: String, ids: List<String>)
}

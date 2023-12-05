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

    @Query("SELECT * FROM $PRODUCT_IMAGES_TABLE_NAME WHERE id = :productId AND clientCode = :clientCode")
    fun findByProductId(clientCode: String, productId: String): Flow<List<ProductPictureEntity>>

    @Upsert
    suspend fun upsert(entities: List<ProductPictureEntity>)

    @Query("DELETE FROM $PRODUCT_IMAGES_TABLE_NAME WHERE id in (:ids) AND clientCode = :clientCode")
    suspend fun delete(clientCode: String, ids: List<String>)
}

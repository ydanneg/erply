package com.ydanneg.erply.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ydanneg.erply.database.model.PRODUCTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCT_IMAGES_TABLE_NAME
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductPictureEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface ErplyProductWithImagesDao {

    @Query("""
        SELECT * FROM $PRODUCTS_TABLE_NAME product 
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME image ON product.id = image.productId 
        WHERE product.clientCode = :clientCode 
            AND product.groupId = :groupId
        """)
    fun findAllByGroupId(clientCode: String, groupId: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>>

    @Query("""
        SELECT * FROM $PRODUCTS_TABLE_NAME product 
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME image ON product.id = image.productId 
        WHERE product.clientCode = :clientCode 
            AND product.groupId = :groupId
            AND name LIKE '%' || :name || '%'
        """)
    fun findAllByGroupIdAndName(clientCode: String, groupId: String, name: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>>

}

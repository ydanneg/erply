package com.ydanneg.erply.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.ydanneg.erply.database.model.PRODUCTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCT_IMAGES_TABLE_NAME
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductPictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErplyProductWithImagesDao {

    @Query(
        """
        SELECT * FROM $PRODUCTS_TABLE_NAME AS product 
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME AS image 
            ON product.id = image.productId 
        WHERE product.clientCode = :clientCode 
            AND product.groupId = :groupId
        ORDER BY product.changed DESC
        """
    )
    fun findAllByGroupId(clientCode: String, groupId: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>>

    @Query(
        """
        SELECT 
            product.id AS id, 
            product.name AS name, 
            product.description AS description, 
            product.price AS price, 
            product.price AS price, 
            image.filename AS filename,
            image.tenant AS tenant 
        FROM $PRODUCTS_TABLE_NAME AS product
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME AS image
            ON product.id = image.productId  
            AND product.clientCode = image.clientCode
        WHERE product.groupId = :groupId
            AND product.clientCode = :clientCode
        ORDER BY product.changed DESC
        """
    )
    fun findAllByGroupIdPageable(clientCode: String, groupId: String): PagingSource<Int, ProductWithImage>

    @Query(
        """
        SELECT 
            product.id AS id, 
            product.name AS name, 
            product.description AS description, 
            product.price AS price, 
            product.price AS price, 
            image.filename AS filename,
            image.tenant AS tenant 
        FROM $PRODUCTS_TABLE_NAME AS product
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME AS image
            ON product.id = image.productId  
            AND product.clientCode = image.clientCode
        WHERE product.groupId = :groupId
            AND product.clientCode = :clientCode
            AND product.name LIKE '%' || :name || '%'
        ORDER BY product.changed DESC
        """
    )
    fun findAllByGroupIdAndNamePageable(clientCode: String, groupId: String, name: String): PagingSource<Int, ProductWithImage>

    data class ProductWithImage(
        val id: String,
        val name: String,
        val description: String?,
        val price: String,
        val filename: String?,
        val tenant: String?
    )

    @Query(
        """
        SELECT * FROM $PRODUCTS_TABLE_NAME AS product 
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME AS image 
            ON product.id = image.productId 
        WHERE product.clientCode = :clientCode 
            AND product.groupId = :groupId
            AND name LIKE '%' || :name || '%'
        ORDER BY product.changed DESC
        """
    )
    fun findAllByGroupIdAndName(clientCode: String, groupId: String, name: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>>

}

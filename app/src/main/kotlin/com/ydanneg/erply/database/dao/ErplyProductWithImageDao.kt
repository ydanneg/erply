package com.ydanneg.erply.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.ydanneg.erply.database.model.PRODUCTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCT_IMAGES_TABLE_NAME
import com.ydanneg.erply.model.ProductWithImage

@Dao
interface ErplyProductWithImageDao {

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
            ON product.clientCode = image.clientCode
                AND product.id = image.productId  
        WHERE product.clientCode = :clientCode
            AND product.groupId = :groupId
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
            ON product.clientCode = image.clientCode
                AND product.id = image.productId  
        WHERE product.clientCode = :clientCode
            AND product.groupId = :groupId
            AND product.name LIKE '%' || :name || '%'
        ORDER BY product.changed DESC
        """
    )
    fun findAllByGroupIdAndNamePageable(clientCode: String, groupId: String, name: String): PagingSource<Int, ProductWithImage>

}

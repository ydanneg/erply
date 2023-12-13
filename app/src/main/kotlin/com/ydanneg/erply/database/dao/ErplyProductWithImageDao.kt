package com.ydanneg.erply.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.ydanneg.erply.database.model.PRODUCTS_FTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCT_IMAGES_TABLE_NAME
import com.ydanneg.erply.model.ProductWithImage

@Suppress("HardCodedStringLiteral")
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
        ORDER BY product.price ASC
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
            AND product.id in (
                SELECT product.id
                FROM $PRODUCTS_TABLE_NAME AS product 
                JOIN $PRODUCTS_FTS_TABLE_NAME AS fts
                    ON product.rowId == fts.rowid 
                WHERE $PRODUCTS_FTS_TABLE_NAME MATCH :search
            )
        ORDER BY product.price ASC
        """
    )
    fun fastSearchAllProducts(clientCode: String, search: String): PagingSource<Int, ProductWithImage>
}

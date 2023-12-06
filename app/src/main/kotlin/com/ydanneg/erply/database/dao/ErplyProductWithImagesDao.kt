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
        SELECT * FROM $PRODUCTS_TABLE_NAME product 
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME image ON product.id = image.productId 
        WHERE product.clientCode = :clientCode 
            AND product.groupId = :groupId
        """
    )
    fun findAllByGroupId(clientCode: String, groupId: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>>

    @Query(
        """
        SELECT 
            products.id as id, 
            products.name as name, 
            products.description as description, 
            products.price as price, 
            products.price as price, 
            product_images.filename as filename,
            product_images.tenant as tenant 
        FROM products
        LEFT JOIN product_images 
            ON products.id = product_images.productId  
            AND products.clientCode = product_images.clientCode
        WHERE products.groupId = :groupId
            AND products.clientCode = :clientCode
        """
    )
    fun findAllByGroupIdPageable(clientCode: String, groupId: String): PagingSource<Int, ProductWithImage>

    @Query(
        """
        SELECT 
            products.id as id, 
            products.name as name, 
            products.description as description, 
            products.price as price, 
            products.price as price, 
            product_images.filename as filename,
            product_images.tenant as tenant 
        FROM products
        LEFT JOIN product_images 
            ON products.id = product_images.productId  
            AND products.clientCode = product_images.clientCode
        WHERE products.groupId = :groupId
            AND products.clientCode = :clientCode
            AND products.name LIKE '%' || :name || '%'
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
        SELECT * FROM $PRODUCTS_TABLE_NAME product 
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME image ON product.id = image.productId 
        WHERE product.clientCode = :clientCode 
            AND product.groupId = :groupId
            AND name LIKE '%' || :name || '%'
        """
    )
    fun findAllByGroupIdAndName(clientCode: String, groupId: String, name: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>>

}

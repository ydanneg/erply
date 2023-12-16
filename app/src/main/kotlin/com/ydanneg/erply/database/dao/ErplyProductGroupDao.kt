package com.ydanneg.erply.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ydanneg.erply.database.model.PRODUCTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCT_GROUPS_TABLE_NAME
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.model.ProductGroupWithProductCount
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
        SELECT
            gr.id AS id,
            gr.parentId AS parentId,
            gr.`order` AS `order`,
            gr.name AS name,
            gr.description AS description,
            gr.changed AS changed,
            COUNT(product.id) AS totalProducts
        FROM $PRODUCT_GROUPS_TABLE_NAME AS gr
        LEFT JOIN $PRODUCTS_TABLE_NAME AS product
            ON gr.clientCode = product.clientCode
                AND gr.id = product.groupId
        WHERE gr.clientCode = :clientCode
        GROUP BY gr.id, gr.parentId, gr.`order`, gr.name, gr.description, gr.changed
        -- HAVING totalProducts > 0
        ORDER BY `order` ASC
        """
    )
    fun getAllWithProductCount(clientCode: String): Flow<List<ProductGroupWithProductCount>>

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

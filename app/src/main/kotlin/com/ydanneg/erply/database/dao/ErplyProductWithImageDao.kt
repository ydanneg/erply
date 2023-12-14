package com.ydanneg.erply.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ydanneg.erply.database.model.PRODUCTS_FTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCTS_TABLE_NAME
import com.ydanneg.erply.database.model.PRODUCT_IMAGES_TABLE_NAME
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductImageEntity
import com.ydanneg.erply.model.ProductWithImage

@Suppress("HardCodedStringLiteral")
@Dao
interface ErplyProductWithImageDao {

    @RawQuery(observedEntities = [ProductEntity::class, ProductImageEntity::class])
    fun getAllByGroupId(query: SupportSQLiteQuery): PagingSource<Int, ProductWithImage>

    fun getAllByGroupIdOderByPrice(clientCode: String, groupId: String, isDesc: Boolean = false): PagingSource<Int, ProductWithImage> =
        getAllByGroupId(allByGroupIdQuery(clientCode, groupId, "price", isDesc.descOrAsc()))

    fun getAllByGroupIdOderByName(clientCode: String, groupId: String, isDesc: Boolean = false): PagingSource<Int, ProductWithImage> =
        getAllByGroupId(allByGroupIdQuery(clientCode, groupId, "name", isDesc.descOrAsc()))

    fun getAllByGroupIdOderByChanged(clientCode: String, groupId: String, isDesc: Boolean = false): PagingSource<Int, ProductWithImage> =
        getAllByGroupId(allByGroupIdQuery(clientCode, groupId, "changed", isDesc.descOrAsc()))

    private fun allByGroupIdQuery(clientCode: String, groupId: String, oderBy: String, direction: String): SimpleSQLiteQuery = SimpleSQLiteQuery(
        """
        SELECT 
            product.id AS id, 
            product.name AS name, 
            product.description AS description, 
            product.price AS price,
            product.changed AS changed,
            image.filename AS filename,
            image.tenant AS tenant 
        FROM $PRODUCTS_TABLE_NAME AS product
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME AS image
            ON product.clientCode = image.clientCode
                AND product.id = image.productId  
        WHERE product.clientCode = ?
            AND product.groupId = ?
        ORDER BY $oderBy $direction
        """.trimIndent(),
        arrayOf(clientCode, groupId)
    )

    @RawQuery(observedEntities = [ProductEntity::class, ProductImageEntity::class])
    fun searchAllProducts(query: SupportSQLiteQuery): PagingSource<Int, ProductWithImage>

    fun searchAllOderByPrice(clientCode: String, search: String, isDesc: Boolean = false): PagingSource<Int, ProductWithImage> =
        searchAllProducts(searchAllQuery(clientCode, search, "price", isDesc.descOrAsc()))

    fun searchAllOderByName(clientCode: String, search: String, isDesc: Boolean = false): PagingSource<Int, ProductWithImage> =
        getAllByGroupId(searchAllQuery(clientCode, search, "name", isDesc.descOrAsc()))

    fun searchAllOderByChanged(clientCode: String, search: String, isDesc: Boolean = false): PagingSource<Int, ProductWithImage> =
        getAllByGroupId(searchAllQuery(clientCode, search, "changed", isDesc.descOrAsc()))

    private fun searchAllQuery(clientCode: String, search: String, oderBy: String, direction: String): SimpleSQLiteQuery = SimpleSQLiteQuery(
        """
        SELECT 
            product.id AS id, 
            product.name AS name, 
            product.description AS description, 
            product.price AS price, 
            image.filename AS filename,
            image.tenant AS tenant 
        FROM $PRODUCTS_TABLE_NAME AS product
        LEFT JOIN $PRODUCT_IMAGES_TABLE_NAME AS image
            ON product.clientCode = image.clientCode
                AND product.id = image.productId  
        WHERE product.clientCode = ?
            AND product.id in (
                SELECT product.id
                FROM $PRODUCTS_TABLE_NAME AS product 
                JOIN $PRODUCTS_FTS_TABLE_NAME AS fts
                    ON product.rowId == fts.rowid 
                WHERE $PRODUCTS_FTS_TABLE_NAME MATCH ?
            )
        ORDER BY $oderBy $direction
        """.trimIndent(),
        arrayOf(clientCode, search)
    )

    private fun Boolean.descOrAsc() = if (this) "DESC" else "ASC"
}

package com.ydanneg.erply.database.model

import androidx.room.Entity
import androidx.room.Index
import com.ydanneg.erply.api.model.ErplyProductType

const val PRODUCTS_TABLE_NAME = "products"
const val GROUPS_TABLE_NAME = "product_groups"
const val PRODUCT_IMAGES_TABLE_NAME = "product_images"

@Entity(
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["changed"])
    ],
    tableName = PRODUCTS_TABLE_NAME,
    primaryKeys = ["id", "clientCode"]
)
data class ProductEntity(
    val id: String,
    val clientCode: String,
    val name: String,
    val type: ErplyProductType = ErplyProductType.PRODUCT,
    val groupId: String,
    val description: String?,
    val price: String,
    val changed: Long
)

@Entity(
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["changed"]),
        Index(value = ["order"])
    ],
    tableName = GROUPS_TABLE_NAME,
    primaryKeys = ["id", "clientCode"]
)
data class ProductGroupEntity(
    val id: String,
    val clientCode: String,
    val name: String,
    val parentId: String,
    val description: String?,
    val changed: Long,
    val order: Int
)

@Entity(
    indices = [
        Index(value = ["productId", "clientCode"])
    ],
    tableName = PRODUCT_IMAGES_TABLE_NAME,
    primaryKeys = ["id", "clientCode"]
)
data class ProductPictureEntity(
    val id: String,
    val clientCode: String,
    val productId: String,
    val tenant: String,
    val filename: String
)

package com.ydanneg.erply.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ydanneg.erply.api.model.ErplyProductType

const val PRODUCTS_TABLE_NAME = "products"
const val GROUPS_TABLE_NAME = "product_groups"
const val PRODUCT_IMAGES_TABLE_NAME = "product_images"

@Entity(
    tableName = PRODUCTS_TABLE_NAME,
    indices = [
        Index(value = ["clientCode"]),
        Index(value = ["clientCode", "id"], unique = true),
        Index(value = ["clientCode", "groupId"]),
        Index(value = ["id"]),
        Index(value = ["name"]),
        Index(value = ["groupId"]),
        Index(value = ["changed"])
    ]
)

data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
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
    tableName = GROUPS_TABLE_NAME,
    indices = [
        Index(value = ["clientCode"]),
        Index(value = ["clientCode", "id"], unique = true),
        Index(value = ["id"]),
        Index(value = ["changed"]),
        Index(value = ["order"])
    ],
)
data class ProductGroupEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val id: String,
    val clientCode: String,
    val name: String,
    val parentId: String,
    val description: String?,
    val changed: Long,
    val order: Int
)

@Entity(
    tableName = PRODUCT_IMAGES_TABLE_NAME,
    indices = [
        Index(value = ["clientCode", "productId"]),
    ]
)
data class ProductPictureEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val id: String,
    val clientCode: String,
    val productId: String,
    val tenant: String,
    val filename: String
)

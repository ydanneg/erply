package com.ydanneg.erply.model


data class ProductGroupWithProductCount(
    val id: String,
    val parentId: String,
    val order: Int,
    val name: String,
    val description: String?,
    val changed: Long,
    val totalProducts: Int
)

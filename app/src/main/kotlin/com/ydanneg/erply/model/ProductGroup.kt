package com.ydanneg.erply.model

data class ProductGroup(
    val id: String,
    val parentId: String,
    val order: Int,
    val name: String,
    val description: String?,
    val changed: Long
)

package com.ydanneg.erply.model

data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val groupId: String,
    val changed: Long,
    val price: String,
)

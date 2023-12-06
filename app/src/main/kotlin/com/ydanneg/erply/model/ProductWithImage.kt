package com.ydanneg.erply.model

data class ProductWithImage(
    val id: String,
    val name: String,
    val description: String?,
    val price: String,
    val filename: String?,
    val tenant: String?
)

package com.ydanneg.erply.model

import java.math.BigDecimal

data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val groupId: String,
    val changed: Long,
    val price: BigDecimal,
)

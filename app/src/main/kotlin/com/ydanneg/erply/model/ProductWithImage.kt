package com.ydanneg.erply.model

import java.math.BigDecimal

data class ProductWithImage(
    val id: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val filename: String?,
    val tenant: String?
)

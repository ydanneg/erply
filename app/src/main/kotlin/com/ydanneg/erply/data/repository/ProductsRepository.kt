package com.ydanneg.erply.data.repository

import com.ydanneg.erply.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    val products: Flow<List<Product>>
}

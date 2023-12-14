package com.ydanneg.erply.data.repository

import androidx.paging.PagingData
import com.ydanneg.erply.model.ProductWithImage
import kotlinx.coroutines.flow.Flow

enum class SortingOrder {
    NAME_ASC,
    NAME_DESC,
    CHANGE_ASC,
    CHANGE_DESC,
    PRICE_ASC,
    PRICE_DESC
}

interface ProductsRepository {
    fun getAllProductsByGroupId(groupId: String, sortingOrder: SortingOrder): Flow<PagingData<ProductWithImage>>
    fun searchAllProducts(search: String, sortingOrder: SortingOrder): Flow<PagingData<ProductWithImage>>
}

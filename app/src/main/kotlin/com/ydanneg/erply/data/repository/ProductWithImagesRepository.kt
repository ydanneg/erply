package com.ydanneg.erply.data.repository

import androidx.paging.PagingData
import com.ydanneg.erply.model.ProductWithImage
import kotlinx.coroutines.flow.Flow

interface ProductWithImagesRepository {
    fun getAllProductsByGroupId(groupId: String): Flow<PagingData<ProductWithImage>>
    fun searchAllProducts(search: String): Flow<PagingData<ProductWithImage>>
}

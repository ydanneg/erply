package com.ydanneg.erply.data.repository

import com.ydanneg.erply.model.ProductGroup
import com.ydanneg.erply.model.ProductGroupWithProductCount
import kotlinx.coroutines.flow.Flow

interface ProductGroupsRepository {
    val productGroups: Flow<List<ProductGroup>>
    val productGroupsWithProductCount: Flow<List<ProductGroupWithProductCount>>
    fun group(groupId: String): Flow<ProductGroup?>
}

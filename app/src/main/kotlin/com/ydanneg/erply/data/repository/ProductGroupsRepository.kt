package com.ydanneg.erply.data.repository

import com.ydanneg.erply.model.ProductGroup
import kotlinx.coroutines.flow.Flow

interface ProductGroupsRepository {
    val productGroups: Flow<List<ProductGroup>>
    fun group(groupId: String): Flow<ProductGroup?>
}

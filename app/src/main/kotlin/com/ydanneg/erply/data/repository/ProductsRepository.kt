package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.data.api.ErplyApiDataSource
import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsRepository @Inject constructor(
    private val erplyApiDataSource: ErplyApiDataSource,
    private val erplyProductDao: ErplyProductDao,
    private val userSessionRepository: UserSessionRepository
) {

    val products = userSessionRepository.withClientCode { erplyProductDao.getAll(it).toModelFlow() }

    fun productsByGroupId(groupId: String) = userSessionRepository.withClientCode {
        erplyProductDao.getAllByGroupId(it, groupId).toModelFlow()
    }

    fun productsByGroupIdAndNameLike(groupId: String, name: String) = userSessionRepository.withClientCode {
        erplyProductDao.findAllByGroupIdAndName(it, groupId, name).toModelFlow()
    }

    suspend fun updateProductsByGroupId(groupId: String): List<ErplyProduct> {
        Log.d(TAG, "Fetching products, group: $groupId")
        val userSession = userSessionRepository.userSession.first()
        return erplyApiDataSource.fetchProductsByGroupId(userSession.token!!, groupId).also { products ->
            Log.d(TAG, "Received ${products.size} products")
            erplyProductDao.insertOrIgnore(products.map { it.toEntity(userSession.clientCode) })
        }
    }

    private fun Flow<List<ProductEntity>>.toModelFlow() = map { entities -> entities.map { it.fromEntity() } }
}

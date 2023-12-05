package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.data.api.ErplyNetworkDataSource
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
    private val erplyProductDao: ErplyProductDao,
    private val userSessionRepository: UserSessionRepository
) : Syncable {

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
        return erplyNetworkDataSource.fetchProductsByGroupId(userSession.token!!, groupId).also { products ->
            Log.d(TAG, "Received ${products.size} products")
            erplyProductDao.upsert(products.map { it.toEntity(userSession.clientCode) })
        }
    }

    private fun Flow<List<ProductEntity>>.toModelFlow() = map { entities -> entities.map { it.fromEntity() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.firstOrNull()
        if (userSession?.token == null) {
            Log.d(TAG, "Sync Products skipped. Not logged in.")
            return false
        }

        val token = userSession.token
        val clientCode = userSession.clientCode
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::productsLastSyncTimestamp,
            serverVersionFetcher = { erplyNetworkDataSource.fetchServerTimestamp(token) },
            deletedListFetcher = { erplyNetworkDataSource.fetchDeletedProductIds(token, it) },
            updatedListFetcher = { erplyNetworkDataSource.fetchProducts(token, it) },
            versionUpdater = { copy(productsLastSyncTimestamp = it) },
            modelDeleter = { erplyProductDao.delete(clientCode, it) },
            modelUpdater = { erplyProductDao.upsert(it.toModelList(clientCode)) },
        )
    }

    private fun List<ErplyProduct>.toModelList(clientCode: String) = map { it.toEntity(clientCode) }
}

package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.data.api.ErplyNetworkDataSource
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.model.isLoggedIn
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductGroupsRepository @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository
) : Syncable {

    val productGroups = userSessionRepository.withClientCode {
        erplyProductGroupDao.getAll(it).toModel()
    }

    fun group(groupId: String) = userSessionRepository.withClientCode {
        erplyProductGroupDao.getById(it, groupId)
    }.map { it.fromEntity() }

    suspend fun updateProductGroups(): List<ErplyProductGroup> {
        Log.d(TAG, "Fetching product groups...")
        val userSession = userSessionRepository.userSession.first()
        return erplyNetworkDataSource.listProductGroups(userSession.token!!).also { groups ->
            Log.d(TAG, "Received ${groups.size} product groups")
            erplyProductGroupDao.upsert(groups.map { it.toEntity(userSession.clientCode) })
        }
    }

    private fun Flow<List<ProductGroupEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.first()
        if (!userSession.isLoggedIn()) {
            Log.d(TAG, "Sync skipped. Not logged in.")
            return false
        }
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::productGroupsLastSyncTimestamp,
            deletedListFetcher = { erplyNetworkDataSource.fetchDeletedProductIds(userSession.token!!, it) },
            updatedListFetcher = { erplyNetworkDataSource.fetchProductGroups(userSession.token!!, it) },
            versionUpdater = { copy(productGroupsLastSyncTimestamp = it) },
            modelDeleter = { erplyProductGroupDao.delete(userSession.clientCode, it) },
            modelUpdater = { erplyProductGroupDao.upsert(it.map { group -> group.toEntity(userSession.clientCode) }) },
        )
    }
}
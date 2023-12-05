package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.data.api.ErplyNetworkDataSource
import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductGroupsRepository @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository
) {

    val productGroups = userSessionRepository.withClientCode {
        erplyProductGroupDao.getAll(it).toModelListFlow()
    }

    fun group(groupId: String) = userSessionRepository.withClientCode {
        erplyProductGroupDao.getById(it, groupId)
    }.map { it?.fromEntity() }

    suspend fun updateProductGroups(): List<ErplyProductGroup> {
        Log.d(TAG, "Fetching product groups...")
        val userSession = userSessionRepository.userSession.first()
        return erplyNetworkDataSource.listProductGroups(userSession.token!!).also { groups ->
            Log.d(TAG, "Received ${groups.size} product groups")
            erplyProductGroupDao.upsert(groups.map { it.toEntity(userSession.clientCode) })
        }
    }

    private fun Flow<List<ProductGroupEntity>>.toModelListFlow() = map { entities -> entities.map { it.fromEntity() } }

}

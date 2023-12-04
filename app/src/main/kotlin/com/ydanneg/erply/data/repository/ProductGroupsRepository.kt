package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.data.api.ErplyApiDataSource
import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ProductGroupsRepository @Inject constructor(
    private val erplyApiDataSource: ErplyApiDataSource,
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository
) {

    val productGroups = userSessionRepository.withClientCode {
        erplyProductGroupDao.getAll(it).toModel()
    }

    fun group(groupId: String) = userSessionRepository.withClientCode {
        erplyProductGroupDao.getById(it, groupId)
    }.map { it.fromEntity() }

    suspend fun loadProductGroups() {
        Log.d(TAG, "Fetching product groups...")
        val userSession = userSessionRepository.userSession.first()
        val groups = erplyApiDataSource.listProductGroups(userSession.token!!)
            .map { it.toEntity(userSession.clientCode) }
        Log.d(TAG, "Received ${groups.size} product groups")
        erplyProductGroupDao.insertOrIgnore(groups)
    }

    private fun Flow<List<ProductGroupEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }
}
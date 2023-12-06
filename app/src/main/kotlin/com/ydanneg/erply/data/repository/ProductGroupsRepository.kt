package com.ydanneg.erply.data.repository

import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductGroupsRepository @Inject constructor(
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository
) {

    val productGroups = userSessionRepository.withClientCode {
        erplyProductGroupDao.getAll(it).toModelListFlow()
    }

    fun group(groupId: String) = userSessionRepository.withClientCode {
        erplyProductGroupDao.getById(it, groupId)
    }.map { it?.fromEntity() }

    private fun Flow<List<ProductGroupEntity>>.toModelListFlow() = map { entities -> entities.map { it.fromEntity() } }

}

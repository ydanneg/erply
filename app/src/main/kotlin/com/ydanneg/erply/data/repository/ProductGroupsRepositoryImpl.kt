package com.ydanneg.erply.data.repository

import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.mappers.fromWithProductCountEntity
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductGroupsRepositoryImpl @Inject constructor(
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository
) : ProductGroupsRepository {

    override val productGroups = userSessionRepository.withClientCode { clientCode ->
        erplyProductGroupDao.getAll(clientCode).map { entities -> entities.map { it.fromEntity() } }
    }

    override val productGroupsWithProductCount = userSessionRepository.withClientCode { clientCode ->
        erplyProductGroupDao.getAllWithProductCount(clientCode).map { entities -> entities.map { it.fromWithProductCountEntity() } }
    }

    override fun group(groupId: String) = userSessionRepository.withClientCode {
        erplyProductGroupDao.getById(it, groupId)
    }.map { it?.fromEntity() }
}

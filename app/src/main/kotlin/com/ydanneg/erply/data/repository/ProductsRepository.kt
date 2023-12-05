package com.ydanneg.erply.data.repository

import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.model.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val erplyProductDao: ErplyProductDao,
    userSessionRepository: UserSessionRepository
) {

    val products = userSessionRepository.withClientCode { erplyProductDao.getAll(it).toModelFlow() }

    private fun Flow<List<ProductEntity>>.toModelFlow() = map { entities -> entities.map { it.fromEntity() } }
}

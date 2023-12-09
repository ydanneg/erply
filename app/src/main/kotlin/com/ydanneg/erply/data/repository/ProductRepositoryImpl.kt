package com.ydanneg.erply.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ydanneg.erply.database.dao.ErplyProductWithImageDao
import com.ydanneg.erply.model.ProductWithImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PAGE_SIZE = 60

@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryImpl @Inject constructor(
    private val erplyProductWithImageDao: ErplyProductWithImageDao,
    private val userSessionRepository: UserSessionRepository
) : ProductRepository {

    override fun getAllProductsByGroupId(groupId: String): Flow<PagingData<ProductWithImage>> {
        return userSessionRepository.userSession.map { it.clientCode }.distinctUntilChanged().flatMapLatest { clientCode ->
            Pager(
                PagingConfig(PAGE_SIZE)
            ) {
                erplyProductWithImageDao.findAllByGroupIdPageable(clientCode, groupId)
            }.flow
        }
    }

    override fun searchAllProducts(search: String): Flow<PagingData<ProductWithImage>> {
        return userSessionRepository.userSession.map { it.clientCode }.distinctUntilChanged().flatMapLatest { clientCode ->
            Pager(
                PagingConfig(PAGE_SIZE)
            ) {
                erplyProductWithImageDao.fastSearchAllProducts(clientCode, "$search*")
            }.flow
        }
    }
}

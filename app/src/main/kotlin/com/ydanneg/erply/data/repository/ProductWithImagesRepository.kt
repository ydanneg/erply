package com.ydanneg.erply.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ydanneg.erply.database.dao.ErplyProductWithImagesDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ProductWithImagesRepository @Inject constructor(
    private val erplyProductWithImagesDao: ErplyProductWithImagesDao,
    private val userSessionRepository: UserSessionRepository
) {

    fun productsWithImagesPageable(groupId: String): Flow<PagingData<ErplyProductWithImagesDao.ProductWithImage>> {
        return userSessionRepository.userSession.map { it.clientCode }.distinctUntilChanged().flatMapLatest { clientCode ->
            Pager(
                PagingConfig(60)
            ) {
                erplyProductWithImagesDao.findAllByGroupIdPageable(clientCode, groupId)
            }.flow
        }
    }

    fun searchProductsWithImagesPageable(groupId: String, search: String): Flow<PagingData<ErplyProductWithImagesDao.ProductWithImage>> {
        return userSessionRepository.userSession.map { it.clientCode }.distinctUntilChanged().flatMapLatest { clientCode ->
            Pager(
                PagingConfig(60)
            ) {
                erplyProductWithImagesDao.findAllByGroupIdAndNamePageable(clientCode, groupId, search)
            }.flow
        }
    }
}

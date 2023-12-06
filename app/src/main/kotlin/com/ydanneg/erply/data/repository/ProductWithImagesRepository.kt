package com.ydanneg.erply.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ydanneg.erply.database.dao.ErplyProductWithImagesDao
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductPictureEntity
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

    fun productsWithImagesByName(groupId: String, search: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>> =
        userSessionRepository.withClientCode {
            erplyProductWithImagesDao.findAllByGroupIdAndName(it, groupId, search)
        }

    fun productsWithImages(groupId: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>> {
        return userSessionRepository.withClientCode {
            erplyProductWithImagesDao.findAllByGroupId(it, groupId)
        }
    }

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

package com.ydanneg.erply.data.repository

import com.ydanneg.erply.database.dao.ErplyProductWithImagesDao
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductPictureEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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
}

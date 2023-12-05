package com.ydanneg.erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.data.api.ErplyNetworkDataSource
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import com.ydanneg.erply.database.dao.ErplyProductImageDao
import com.ydanneg.erply.database.dao.ErplyProductWithImagesDao
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductPictureEntity
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ProductImagesRepository @Inject constructor(
    private val erplyNetworkDataSource: ErplyNetworkDataSource,
    private val erplyProductImageDao: ErplyProductImageDao,
    private val erplyProductWithImagesDao: ErplyProductWithImagesDao,
    private val userSessionRepository: UserSessionRepository
) : Syncable {

    suspend fun productsWithImagesByName(groupId: String, search: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>> =
        userSessionRepository.withClientCode {
            erplyProductWithImagesDao.findAllByGroupIdAndName(it, groupId, search)
        }

    suspend fun productsWithImages(groupId: String): Flow<Map<ProductEntity, List<ProductPictureEntity>>> {
        return userSessionRepository.withClientCode {
            erplyProductWithImagesDao.findAllByGroupId(it, groupId)
        }
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.firstOrNull()
        if (userSession?.token == null) {
            Log.d(TAG, "Sync Products skipped. Not logged in.")
            return false
        }

        val token = userSession.token
        val clientCode = userSession.clientCode
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::picturesLastSyncTimestamp,
            serverVersionFetcher = { erplyNetworkDataSource.fetchServerTimestamp(token) },
            deletedListFetcher = { flowOf() },
            updatedListFetcher = { erplyNetworkDataSource.fetchAllImages(token, it) },
            versionUpdater = { copy(picturesLastSyncTimestamp = it) },
            modelDeleter = { erplyProductImageDao.delete(clientCode, it) },
            modelUpdater = { erplyProductImageDao.upsert(it.toModelList(clientCode)) },
        )
    }

    private fun List<ErplyProductPicture>.toModelList(clientCode: String) = map { it.toEntity(clientCode) }
}

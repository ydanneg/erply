package com.ydanneg.erply.domain.sync

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.database.dao.ErplyProductImageDao
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.domain.GetAllProductImagesFromRemoteUseCase
import com.ydanneg.erply.domain.GetServerVersionUseCase
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SyncProductImagesUseCase @Inject constructor(
    private val erplyProductImageDao: ErplyProductImageDao,
    private val userSessionRepository: UserSessionRepository,
    private val getAllProductImagesFromRemoteUseCase: GetAllProductImagesFromRemoteUseCase,
    private val getServerVersionUseCase: GetServerVersionUseCase
) : Syncable {

    override suspend operator fun invoke(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.firstOrNull()
        if (userSession?.token == null) {
            Log.d(TAG, "Sync Products skipped. Not logged in.")
            return false
        }

        val clientCode = userSession.clientCode
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::picturesLastSyncTimestamp,
            serverVersionFetcher = { getServerVersionUseCase.invoke() },
            deletedListFetcher = { flowOf() },
            updatedListFetcher = { getAllProductImagesFromRemoteUseCase.invoke(it) },
            versionUpdater = { copy(picturesLastSyncTimestamp = it) },
            modelDeleter = { erplyProductImageDao.delete(clientCode, it) },
            modelUpdater = { erplyProductImageDao.insertOrUpdate(it.toModelList(clientCode)) },
        )
    }

    private fun List<ErplyProductPicture>.toModelList(clientCode: String) = map { it.toEntity(clientCode) }
}

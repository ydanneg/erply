package com.ydanneg.erply.domain.sync

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.domain.GetAllDeletedProductsFromRemoteUseCase
import com.ydanneg.erply.domain.GetAllProductsFromRemoteUseCase
import com.ydanneg.erply.domain.GetServerVersionUseCase
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SyncProductsUseCase @Inject constructor(
    private val getAllProductsFromRemoteUseCase: GetAllProductsFromRemoteUseCase,
    private val getAllDeletedProductsFromRemoteUseCase: GetAllDeletedProductsFromRemoteUseCase,
    private val getServerVersionUseCase: GetServerVersionUseCase,
    private val erplyProductDao: ErplyProductDao,
    private val userSessionRepository: UserSessionRepository
) : Syncable {

    override suspend operator fun invoke(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.firstOrNull()
        if (userSession?.token == null) {
            Log.d(TAG, "Sync Products skipped. Not logged in.")
            return false
        }

        val clientCode = userSession.clientCode
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::productsLastSyncTimestamp,
            serverVersionFetcher = { getServerVersionUseCase.invoke() },
            deletedListFetcher = { getAllDeletedProductsFromRemoteUseCase(it) },
            updatedListFetcher = { getAllProductsFromRemoteUseCase(it) },
            versionUpdater = { copy(productsLastSyncTimestamp = it) },
            modelDeleter = { erplyProductDao.delete(clientCode, it) },
            modelUpdater = { erplyProductDao.insertOrUpdate(it.toModelList(clientCode)) },
        )
    }

    private fun List<ErplyProduct>.toModelList(clientCode: String) = map { it.toEntity(clientCode) }
}

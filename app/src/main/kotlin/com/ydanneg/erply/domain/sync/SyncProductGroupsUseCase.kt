package com.ydanneg.erply.domain.sync

import android.util.Log
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.domain.GetAllProductGroupsFromRemoteUseCase
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import com.ydanneg.erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SyncProductGroupsUseCase @Inject constructor(
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository,
    private val getAllProductGroupsFromRemoteUseCase: GetAllProductGroupsFromRemoteUseCase
) : Syncable {

    override suspend operator fun invoke(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.firstOrNull()
        if (userSession?.token == null) {
            Log.d(TAG, "Sync Product groups skipped. Not logged in.")
            return false
        }

        val clientCode = userSession.clientCode
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::productGroupsLastSyncTimestamp,
            serverVersionFetcher = { 0L },
            deletedListFetcher = { flowOf() }, // not supported?
            updatedListFetcher = { getAllProductGroupsFromRemoteUseCase.invoke(it) },
            versionUpdater = { copy(productGroupsLastSyncTimestamp = it) },
            modelDeleter = { erplyProductGroupDao.delete(clientCode, it) },
            modelUpdater = { erplyProductGroupDao.insertOrUpdate(it.toModelList(clientCode)) },
        )
    }

    private fun List<ErplyProductGroup>.toModelList(clientCode: String) = map { it.toEntity(clientCode) }
}

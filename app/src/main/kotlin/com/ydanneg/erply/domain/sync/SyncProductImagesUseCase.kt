package com.ydanneg.erply.domain.sync

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.database.dao.ErplyProductImageDao
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.domain.GetAllDeletedProductImageIdsFromRemoteUseCase
import com.ydanneg.erply.domain.GetAllProductImagesFromRemoteUseCase
import com.ydanneg.erply.domain.GetServerVersionUseCase
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import kotlinx.coroutines.flow.firstOrNull
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SyncProductImagesUseCase @Inject constructor(
    private val erplyProductImageDao: ErplyProductImageDao,
    private val userSessionRepository: UserSessionRepository,
    private val getAllProductImagesFromRemoteUseCase: GetAllProductImagesFromRemoteUseCase,
    private val getAllDeletedProductImageIdsFromRemoteUseCase: GetAllDeletedProductImageIdsFromRemoteUseCase,
    private val getServerVersionUseCase: GetServerVersionUseCase
) : Syncable {
    private val log = LoggerFactory.getLogger("SyncProductImagesUseCase")

    override suspend operator fun invoke(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.firstOrNull()
        if (userSession?.token == null) {
            log.debug("Sync Products skipped. Not logged in.")//NON-NLS
            return false
        }

        val clientCode = userSession.clientCode
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::picturesLastSyncTimestamp,
            serverVersionFetcher = { getServerVersionUseCase.invoke() },
            deletedListFetcher = { getAllDeletedProductImageIdsFromRemoteUseCase.invoke(it) },
            updatedListFetcher = { getAllProductImagesFromRemoteUseCase.invoke(it) },
            versionUpdater = { copy(picturesLastSyncTimestamp = it) },
            modelDeleter = { erplyProductImageDao.delete(clientCode, it) },
            modelUpdater = { erplyProductImageDao.insertOrUpdate(it.map { image -> image.toEntity(clientCode) }) },
        )
    }
}

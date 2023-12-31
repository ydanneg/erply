package com.ydanneg.erply.domain.sync

import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.mappers.toEntity
import com.ydanneg.erply.domain.GetAllDeletedProductsFromRemoteUseCase
import com.ydanneg.erply.domain.GetAllProductsFromRemoteUseCase
import com.ydanneg.erply.domain.GetServerVersionUseCase
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.sync.Syncable
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.changeListSync
import kotlinx.coroutines.flow.firstOrNull
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SyncProductsUseCase @Inject constructor(
    private val getAllProductsFromRemoteUseCase: GetAllProductsFromRemoteUseCase,
    private val getAllDeletedProductsFromRemoteUseCase: GetAllDeletedProductsFromRemoteUseCase,
    private val getServerVersionUseCase: GetServerVersionUseCase,
    private val erplyProductDao: ErplyProductDao,
    private val userSessionRepository: UserSessionRepository
) : Syncable {

    private val log = LoggerFactory.getLogger("SyncProductsUseCase")

    override suspend operator fun invoke(synchronizer: Synchronizer): Boolean {
        val userSession = userSessionRepository.userSession.firstOrNull()
        if (userSession?.token == null) {
            log.debug("Sync Products skipped. Not logged in.")//NON-NLS
            return false
        }

        val clientCode = userSession.clientCode
        return synchronizer.changeListSync(
            versionReader = LastSyncTimestamps::productsLastSyncTimestamp,
            serverVersionFetcher = { getServerVersionUseCase.invoke() },
            deletedListFetcher = { getAllDeletedProductsFromRemoteUseCase.invoke(it) },
            updatedListFetcher = { getAllProductsFromRemoteUseCase.invoke(it) },
            versionUpdater = { copy(productsLastSyncTimestamp = it) },
            modelDeleter = { erplyProductDao.delete(clientCode, it) },
            modelUpdater = { erplyProductDao.insertOrUpdate(it.map { it.toEntity(clientCode) }) },
        )
    }
}

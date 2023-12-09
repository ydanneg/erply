package com.ydanneg.erply.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.datastore.UserPreferencesDataSource
import com.ydanneg.erply.di.Dispatcher
import com.ydanneg.erply.di.ErplyDispatchers
import com.ydanneg.erply.domain.sync.SyncProductGroupsUseCase
import com.ydanneg.erply.domain.sync.SyncProductImagesUseCase
import com.ydanneg.erply.domain.sync.SyncProductsUseCase
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.sync.SyncConstraints
import com.ydanneg.erply.sync.Synchronizer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncProductsUseCase: SyncProductsUseCase,
    private val syncProductGroupsUseCase: SyncProductGroupsUseCase,
    private val syncProductImagesUseCase: SyncProductImagesUseCase,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val userSessionRepository: UserSessionRepository,
    @Dispatcher(ErplyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams), Synchronizer {

    private val log = LoggerFactory.getLogger("SyncWorker")

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        // Login to be sure token is not expired within a sync job
        runCatching {
            userSessionRepository.tryLogin()
        }.getOrNull() ?: return@withContext Result.failure()

        val syncedSuccessfully = coroutineScope {
            val deferredGroups = async { syncProductGroupsUseCase.sync() }
            val deferredImages = async { syncProductImagesUseCase.sync() }
            val deferredProducts = async { syncProductsUseCase.sync() }
            awaitAll(deferredGroups, deferredImages, deferredProducts).all { it }
        }
        log.info("doWork complete: $syncedSuccessfully")//NON-NLS

        if (syncedSuccessfully) Result.success() else Result.retry()
    }

    override suspend fun getChangeListVersions(): LastSyncTimestamps =
        userPreferencesDataSource.userPreferences
            .map { it.lastSyncTimestamps }
            .firstOrNull() ?: LastSyncTimestamps()

    override suspend fun updateChangeListVersions(
        update: LastSyncTimestamps.() -> LastSyncTimestamps,
    ) {
        val clientCode = userSessionRepository.userSession.first().clientCode
        userPreferencesDataSource.updateChangeListVersion(clientCode, update)
    }

    companion object {
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}

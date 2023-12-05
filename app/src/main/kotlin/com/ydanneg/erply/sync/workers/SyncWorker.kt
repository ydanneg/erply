package com.ydanneg.erply.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import com.ydanneg.erply.data.datastore.UserPreferencesDataSource
import com.ydanneg.erply.data.repository.ProductGroupsRepository
import com.ydanneg.erply.data.repository.ProductsRepository
import com.ydanneg.erply.di.Dispatcher
import com.ydanneg.erply.di.ErplyDispatchers
import com.ydanneg.erply.sync.SyncConstraints
import com.ydanneg.erply.sync.Synchronizer
import com.ydanneg.erply.sync.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productsRepository: ProductsRepository,
    private val productGroupsRepository: ProductGroupsRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    @Dispatcher(ErplyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams), Synchronizer {

//    override suspend fun getForegroundInfo(): ForegroundInfo =
//        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        // First sync the repositories in parallel
        setForeground(appContext.syncForegroundInfo())
        val syncedSuccessfully = awaitAll(
            async { productGroupsRepository.sync() },
            async {
                // tiny delay to ensure they both don't re-authentication
                delay(500)
                productsRepository.sync()
            },
        ).all { it }

        if (syncedSuccessfully) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    override suspend fun getChangeListVersions(): LastSyncTimestamps =
        userPreferencesDataSource.userPreferences
            .map { it.lastSyncTimestamps }
            .firstOrNull() ?: LastSyncTimestamps()

    override suspend fun updateChangeListVersions(
        update: LastSyncTimestamps.() -> LastSyncTimestamps,
    ) = userPreferencesDataSource.updateChangeListVersion(update)

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}

package com.ydanneg.erply.sync

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ydanneg.erply.sync.workers.SyncWorker
import com.ydanneg.erply.util.LogUtils.TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import javax.inject.Inject

class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val log = LoggerFactory.getLogger("WorkManagerSyncManager")

    val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(SyncWorkName)
            .map(List<WorkInfo>::anyRunning)
            .conflate()

    fun requestSync() {
        log.info("Requesting sync...")//NON-NLS
        val workManager = WorkManager.getInstance(context)
        // Run sync on app startup and ensure only one sync worker runs at any time
        workManager.enqueueUniqueWork(
            SyncWorkName,
            ExistingWorkPolicy.KEEP,
            SyncWorker.startUpSyncWork(),
        )
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }

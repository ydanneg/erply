package com.ydanneg.erply.sync

import com.ydanneg.erply.model.LastSyncTimestamps
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 */
interface Synchronizer {
    suspend fun getChangeListVersions(): LastSyncTimestamps

    suspend fun updateChangeListVersions(update: LastSyncTimestamps.() -> LastSyncTimestamps)

    /**
     * Syntactic sugar to call [Syncable.invoke] while omitting the synchronizer argument
     */
    suspend fun Syncable.sync() = this@sync(this@Synchronizer)
}

/**
 * Interface marker for a class that is synchronized with a remote source. Syncing must not be
 * performed concurrently and it is the [Synchronizer]'s responsibility to ensure this.
 */
interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     */
    suspend operator fun invoke(synchronizer: Synchronizer): Boolean
}

private val log = LoggerFactory.getLogger("suspendRunCatching")

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    log.warn("Failed to evaluate a suspendRunCatchingBlock. Returning failure Result", exception)//NON-NLS
    Result.failure(exception)
}

suspend fun <T> Synchronizer.changeListSync(
    versionReader: (LastSyncTimestamps) -> Long,
    serverVersionFetcher: suspend () -> Long,
    updatedListFetcher: suspend (Long) -> Flow<List<T>>,
    deletedListFetcher: suspend (Long) -> Flow<List<String>>,
    versionUpdater: LastSyncTimestamps.(Long) -> LastSyncTimestamps,
    modelDeleter: suspend (List<String>) -> Unit,
    modelUpdater: suspend (List<T>) -> Unit,
) = suspendRunCatching {
    val latestSyncVersion = serverVersionFetcher()
    val previousSyncVersion = versionReader(getChangeListVersions())

    coroutineScope {
        launch {
            if (previousSyncVersion > 0)
                deletedListFetcher(previousSyncVersion).collect(modelDeleter)
        }
        launch {
            updatedListFetcher(previousSyncVersion).collect(modelUpdater)
        }
    }

    updateChangeListVersions {
        versionUpdater(latestSyncVersion)
    }
}.isSuccess

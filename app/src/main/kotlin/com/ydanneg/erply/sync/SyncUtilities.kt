/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ydanneg.erply.sync

import android.util.Log
import com.ydanneg.erply.data.datastore.LastSyncTimestamps
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 */
interface Synchronizer {
    suspend fun getChangeListVersions(): LastSyncTimestamps

    suspend fun updateChangeListVersions(update: LastSyncTimestamps.() -> LastSyncTimestamps)

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
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

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
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
                deletedListFetcher(previousSyncVersion).collect {
                    modelDeleter(it)
                }
        }
        launch {
            updatedListFetcher(previousSyncVersion)
                .collect {
                    modelUpdater(it)
                }
        }
//        Pair(deferredDeleted.await(), deferredUpdated.await())
    }
//
//    if (deleted.isEmpty() && updated.isEmpty()) {
//        // no changes, return
//        return@suspendRunCatching true
//    }

//    // Delete models that have been deleted server-side
//    modelDeleter(deleted)
//    // Using the change list, pull down and save the changes (akin to a git pull)
//    modelUpdater(updated)

    // Update the last synced version (akin to updating local git HEAD)
    updateChangeListVersions {
        versionUpdater(latestSyncVersion)
    }
}.isSuccess

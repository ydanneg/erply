package com.ydanneg.erply.model

/**
 * Class summarizing the local version of each model for sync
 */
data class LastSyncTimestamps(
    val productGroupsLastSyncTimestamp: Long = 0,
    val productsLastSyncTimestamp: Long = 0,
    val picturesLastSyncTimestamp: Long = 0
)

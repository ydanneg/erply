package com.ydanneg.erply.api.client.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield

typealias PageFetcher<T> = suspend (Int, Int) -> List<T>

fun <T> fetchAllPages(pageSize: Int, fetchPage: PageFetcher<T>): Flow<List<T>> {
    return flow {
        var skip = 0
        while (true) {
            val products = fetchPage(skip, pageSize)
            if (products.isEmpty()) {
                break
            }
            emit(products)
            yield()
            skip += pageSize
        }
    }
}

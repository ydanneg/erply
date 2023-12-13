@file:Suppress("HardCodedStringLiteral")

package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.client.util.apiFilter
import com.ydanneg.erply.api.client.util.executeOrThrow
import com.ydanneg.erply.api.client.util.fetchAllPages
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.flow.Flow


class ProductsApi internal constructor(private val httpClient: HttpClient) {

    suspend fun fetchTimestamp(token: String): Long = executeOrThrow {
        // take only one item as an optimization
        httpClient.get("v1/product?fields=id&skip=0&take=1") {
            headers {
                append("jwt", token)
            }
        }.let {
            it.headers["request-time-unix"]?.toLong() ?: 0
        }
    }

    suspend fun fetchAllProductGroups(token: String, changedSince: Long? = null): Flow<List<ErplyProductGroup>> =
        fetchAllPages(PAGE_SIZE) { skip, take ->
            fetchProductGroups(token = token, changedSince = changedSince, skip = skip, take = take)
        }

    private suspend fun fetchProductGroups(token: String, changedSince: Long? = null, skip: Int = 0, take: Int = PAGE_SIZE): List<ErplyProductGroup> =
        executeOrThrow {
            val filter = apiFilter {
                eq("show_in_webshop", 1)
                if (changedSince != null) {
                    gte("changed", changedSince)
                }
            }
            val url = "v1/product/group?skip=$skip&take=$take&withTotalCount=1&filter=$filter"
            httpClient.get(url) {
                headers {
                    append("jwt", token)
                }
            }.body()
        }

    suspend fun fetchAllProducts(token: String, changedSince: Long? = null): Flow<List<ErplyProduct>> =
        fetchAllPages(PAGE_SIZE) { skip, take ->
            fetchProducts(token, changedSince, skip = skip, take = take)
        }

    private suspend fun fetchProducts(token: String, changedSince: Long? = null, skip: Int = 0, take: Int = PAGE_SIZE): List<ErplyProduct> =
        executeOrThrow {
            val fields = arrayOf("id", "type", "group_id", "name", "price", "changed", "description").joinToString(",")
            val filter = apiFilter {
                eq("displayed_in_webshop", 1)
                eq("status", "ACTIVE")
                if (changedSince != null) {
                    gte("changed", changedSince)
                }
            }
            val url = "v1/product?fields=$fields&withTotalCount=true&skip=$skip&take=$take&filter=$filter"
            httpClient.get(url) {
                headers {
                    append("jwt", token)
                }
            }.body()
        }

    suspend fun fetchAllDeletedProductIds(token: String, changedSince: Long): Flow<List<String>> =
        fetchAllPages(PAGE_SIZE) { skip, take ->
            fetchDeletedProductIds(token, changedSince, skip = skip, take = take)
        }

    private suspend fun fetchDeletedProductIds(token: String, changedSince: Long, skip: Int = 0, take: Int = PAGE_SIZE): List<String> =
        executeOrThrow {
            val url = "v1/product/deleted/ids?skip=$skip&take=$take"
            httpClient.get(url) {
                headers {
                    append("If-Modified-Since", changedSince.toString())
                    append("jwt", token)
                }
            }.body()
        }

    companion object {
        private const val PAGE_SIZE = 1000
    }
}

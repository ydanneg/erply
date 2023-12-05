package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpStatusCode

class ProductsApi internal constructor(private val httpClient: HttpClient) {

    suspend fun fetchTimestamp(token: String): Long {
        // take only one item as an optimization
        return httpClient.get("v1/product?fields=id&skip=0&take=1") {
            headers {
                append("jwt", token)
            }
        }.let {
            it.headers["request-time-unix"]?.toLong() ?: 0
        }
    }

    suspend fun listProductGroups(token: String): List<ErplyProductGroup> {
        val url = "v1/product/group?skip=0&take=$PAGE_SIZE&filter=[$showInWebFilter]&withTotalCount=1"
        return executeOrThrow {
            httpClient.get(url) {
                headers {
                    append("jwt", token)
                }
            }.body()
        }
    }

    suspend fun fetchProductsByGroupId(token: String, groupId: String): List<ErplyProduct> = executeOrThrow {
        fetchProducts(token = token, filter = groupIdFilter(groupId)) //TODO: add more filters (e.g. 'show_in_webshop')
    }

    suspend fun fetchProductGroups(token: String, changedSince: Long? = null, skip: Int = 0, take: Int = PAGE_SIZE): List<ErplyProductGroup> =
        executeOrThrow {
            val url = "v1/product/group?skip=$skip&take=$take&withTotalCount=1".let { url ->
                if (changedSince != null) "$url&filter=[${changedFilter(changedSince)}]" else url
            }
            httpClient.get(url) {
                headers {
                    append("jwt", token)
                }
            }.body()
        }

    suspend fun fetchDeletedProductGroups(token: String, changedSince: Long? = null): List<String> {
        TODO("PIM has no such functionality?")
    }

    suspend fun fetchProducts(token: String, changedSince: Long? = null, skip: Int = 0, take: Int = PAGE_SIZE): List<ErplyProduct> = executeOrThrow {
        val fields = fields("id", "type", "group_id", "name", "price", "changed")
        val url = "v1/product?fields=$fields&withTotalCount=true&skip=$skip&take=$take".let { url ->
            val filter = showInWebFilter.let {
                if (changedSince != null) "$it,\"and\",${changedFilter(changedSince)}" else it
            }
            "$url&filter=[$filter]"
        }
        httpClient.get(url) {
            headers {
                append("jwt", token)
            }
        }.body()
    }

    suspend fun fetchDeletedProductIds(token: String, changedSince: Long): List<String> {
        val url = "v1/product/deleted/ids?skip=0&take=$PAGE_SIZE"
        return executeOrThrow {
            httpClient.get(url) {
                headers {
                    append("If-Modified-Since", changedSince.toString())
                    append("jwt", token)
                }
            }.body()
        }
    }
//
//    suspend fun fetchAllProducts(token: String): Flow<List<ErplyProduct>> {
//        return flow {
//            var skip = 0
//            while (true) {
//                val products = fetchProducts(token, skip = skip, take = PAGE_SIZE)
//                if (products.isEmpty()) {
//                    break
//                }
//                emit(products)
//                yield()
//                skip += PAGE_SIZE
//            }
//        }
//    }

    private suspend fun fetchProducts(token: String, filter: String? = null, skip: Int = 0, take: Int = 1000): List<ErplyProduct> {
        val fields = fields("id", "type", "group_id", "name", "price", "changed")
        val url = "v1/product?fields=$fields&withTotalCount=true&skip=$skip&take=$take".let {
            if (filter != null) "$it&filter=$filter" else it
        }
        return httpClient.get(url) {
            headers {
                append("jwt", token)
            }
        }.body()
    }

    private fun groupIdFilter(groupId: String) = """
        [["group_id","=","$groupId"]]
        """.trimIndent()

    private val showInWebFilter = """
        ["show_in_webshop","=","1"]
        """.trimIndent()

    private fun changedFilter(changedSince: Long) = """
        ["changed",">=","$changedSince"]
        """.trimIndent()


    private suspend fun <T> executeOrThrow(block: suspend () -> T): T = try {
        block()
    } catch (e: Throwable) {
        e.handleError()
    }

    private fun fields(vararg fields: String) = fields.joinToString(",")

    private fun Throwable.handleError(): Nothing {
        throw when (this) {
            is ClientRequestException -> when (response.status) {
                HttpStatusCode.Unauthorized -> ErplyApiException(ErplyApiError.Unauthorized)
                HttpStatusCode.Forbidden -> ErplyApiException(ErplyApiError.AccessDenied)
                else -> throw ErplyApiException(ErplyApiError.Unknown)
            }

            else -> throw ErplyApiException(ErplyApiError.Unknown)
        }
    }

    companion object {
        private const val PAGE_SIZE = 1000
    }
}

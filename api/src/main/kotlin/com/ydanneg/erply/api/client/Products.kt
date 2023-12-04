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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield

const val PAGE_SIZE = 1000

class ProductsApi internal constructor(private val httpClient: HttpClient) {

    suspend fun listProductGroups(token: String): List<ErplyProductGroup> {
        val fields = fields("id", "parent_id", "order", "name", "changed")
        val url = "v1/product/group?fields=$fields&skip=0&take=1000&filter=$showInWebFilter&withTotalCount=1"
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

    suspend fun fetchAllProducts(token: String): Flow<List<ErplyProduct>> {
        return flow {
            var skip = 0
            while (true) {
                val products = fetchProducts(token, skip = skip, take = PAGE_SIZE)
                if (products.isEmpty()) {
                    break
                }
                emit(products)
                yield()
                skip += PAGE_SIZE
            }
        }
    }

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
        [["show_in_webshop","=","1"]]
        """.trimIndent()


    private suspend fun <T> executeOrThrow(block: suspend () -> T): T =
        try {
            block()
        } catch (e: Throwable) {
            e.handleError()
        }

    private fun fields(vararg fields: String) = fields.joinToString(",")

    private fun Throwable.handleError(): Nothing {
        throw when (this) {
            is ClientRequestException -> when (response.status) {
                HttpStatusCode.Unauthorized -> ErplyApiException(ErplyApiError.SessionExpired)
                HttpStatusCode.Forbidden -> ErplyApiException(ErplyApiError.AccessDenied)
                else -> throw ErplyApiException(ErplyApiError.Unknown)
            }
            else -> throw ErplyApiException(ErplyApiError.Unknown)
        }
    }
}
package com.ydanneg.erply.api

import com.ydanneg.erply.model.ErplyProduct
import com.ydanneg.erply.model.ErplyProductGroup
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.yield

const val PAGE_SIZE = 1000

class ProductsApi internal constructor(private val httpClient: HttpClient) {

    suspend fun listProductGroups(token: String): List<ErplyProductGroup> {
        val url = "https://api-pim-eu10.erply.com/v1/product/group?fields=id,parent_id,order,name,changed&filter=$showInWebFilter"
        return httpClient.get(url) {
            headers {
                append("jwt", token)
            }
        }.body()
    }

    suspend fun fetchProductsByGroupId(token: String, groupId: String): List<ErplyProduct> =
        fetchProducts(token = token, filter = groupIdFilter(groupId)) //TODO: add more filters (e.g. 'show_in_webshop')

    suspend fun fetchAllProducts(token: String): List<ErplyProduct> {
        var skip = 0

        val result = mutableListOf<ErplyProduct>()
        while (true) {
            val products = fetchProducts(token, skip = skip, take = PAGE_SIZE)
            result.addAll(products)
            if (products.isEmpty()) {
                break
            }
            yield()
            skip += PAGE_SIZE
        }
        return result
    }

    private suspend fun fetchProducts(token: String, filter: String? = null, skip: Int = 0, take: Int = 1000): List<ErplyProduct> {
        val url =
            "https://api-pim-eu10.erply.com/v1/product?fields=id,type,group_id,name,price,changed&withTotalCount=true&skip=$skip&take=$take".let {
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
}
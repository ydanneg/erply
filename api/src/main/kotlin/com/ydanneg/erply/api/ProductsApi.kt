package com.ydanneg.erply.api

import com.ydanneg.erply.model.ErplyProduct
import com.ydanneg.erply.model.ErplyProductGroup
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers

class ProductsApi internal constructor(private val httpClient: HttpClient) {

    suspend fun listProductGroups(token: String): List<ErplyProductGroup> {
        val url = "https://api-pim-eu10.erply.com/v1/product/group?fields=id,parent_id,order,name&filter=$showInWebFilter"
        return httpClient.get(url) {
            headers {
                append("jwt", token)
            }
        }.body()
    }

    suspend fun listProducts(token: String, groupId: String? = null): List<ErplyProduct> {
        val url = "https://api-pim-eu10.erply.com/v1/product?fields=id,type,group_id,name,price".let {
            if (groupId != null) "$it&filter=${groupIdFilter(groupId)}" else it
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
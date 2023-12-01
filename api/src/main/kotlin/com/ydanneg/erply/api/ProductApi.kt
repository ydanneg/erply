package com.ydanneg.erply.api

import com.ydanneg.erply.model.ErplyProduct
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers

class ProductApi internal constructor(private val httpClient: HttpClient) {

    suspend fun listProducts(token: String): List<ErplyProduct> {
        return httpClient.get("https://api-pim-eu10.erply.com/v1/product?fields=id%2Ctype%2Cgroup_id%2Cname%2Cprice") {
            headers {
                append("jwt", token)
            }
        }.body()
    }
}
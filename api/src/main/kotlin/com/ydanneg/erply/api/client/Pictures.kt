package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.api.model.ErplyProductPicturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield

class CdnApi internal constructor(private val httpClient: HttpClient, private val configuration: ErplyApiClientConfiguration) {

    fun fetchAllProductPictures(token: String, changedSince: Long? = null): Flow<List<ErplyProductPicture>> {
        return flow {
            var page = 0
            while (true) {
                val response = fetchProductPictures(token, changedSince, page)
                if (response.images.isEmpty()) {
                    break
                }
                emit(response.images)
                if (response.recordsReturned < response.recordsPerPage) {
                    break
                }
                yield()
                page++
            }
        }
    }

    private suspend fun fetchProductPictures(token: String, changedSince: Long? = null, page: Int = 0): ErplyProductPicturesResponse {
        val url = "images?page=$page&isDeleted=false".let {
            if (changedSince != null) "$it&changedSince=$changedSince" else it
        }
        return httpClient.get(url) {
            headers {
                append("jwt", token)
            }
        }.body()
    }

    @Suppress("unused")
    fun downloadableProductPictureUrl(tenant: String, filename: String) = "${configuration.cdnBaseUrl}/images/$tenant/$filename"
}

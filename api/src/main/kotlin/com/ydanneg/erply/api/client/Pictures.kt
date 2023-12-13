package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.client.util.executeOrThrow
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

    fun fetchAllProductPictures(token: String, changedSince: Long? = null): Flow<List<ErplyProductPicture>> =
        fetchAllPages { page ->
            fetchProductPictures(
                token = token,
                page = page,
                changedSince = changedSince,
                isDeleted = false
            )
        }

    fun fetchAllDeletedProductPictures(token: String, changedSince: Long? = null): Flow<List<ErplyProductPicture>> =
        fetchAllPages { page ->
            fetchProductPictures(
                token = token,
                page = page,
                changedSince = changedSince,
                isDeleted = true
            )
        }

    private suspend fun fetchProductPictures(
        token: String,
        page: Int,
        changedSince: Long? = null,
        isDeleted: Boolean = false
    ): ErplyProductPicturesResponse = executeOrThrow {
        val url = "images?page=$page&isDeleted=$isDeleted".let {
            if (changedSince != null) "$it&changedSince=$changedSince" else it
        }
        httpClient.get(url) {
            headers {
                append("jwt", token)
            }
        }.body()
    }

    private fun fetchAllPages(fetchPage: suspend (Int) -> ErplyProductPicturesResponse): Flow<List<ErplyProductPicture>> = flow {
        var page = 0
        while (true) {
            val response = fetchPage(page)
            if (response.images.isEmpty()) {
                break
            }
            emit(response.images)
            if (response.recordsReturned < response.recordsPerPage) {
                break
            }
            page++
            yield()
        }
    }

    @Suppress("unused")
    fun downloadableProductPictureUrl(tenant: String, filename: String) = "${configuration.cdnBaseUrl}/images/$tenant/$filename"
}

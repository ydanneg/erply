package com.ydanneg.erply.api.client.util

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode


internal suspend fun <T> executeOrThrow(block: suspend () -> T): T = try {
    block()
} catch (e: Throwable) {
    e.handleError()
}
internal fun Throwable.handleError(): Nothing {
    throw when (this) {
        is ClientRequestException -> when (response.status) {
            HttpStatusCode.Unauthorized -> ErplyApiException(ErplyApiError.Unauthorized)
            HttpStatusCode.Forbidden -> ErplyApiException(ErplyApiError.AccessDenied)
            else -> throw ErplyApiException(ErplyApiError.Unknown)
        }
        else -> throw ErplyApiException(ErplyApiError.Unknown)
    }
}

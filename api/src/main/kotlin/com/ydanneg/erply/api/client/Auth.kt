package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.api.model.ErplyApiException
import com.ydanneg.erply.api.model.ErplyResponse
import com.ydanneg.erply.api.model.ErplyVerifiedUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType

class AuthApi internal constructor(private val httpClient: HttpClient) {
    suspend fun login(clientCode: String, username: String, password: String): ErplyVerifiedUser {
        return httpClient.post("https://$clientCode.erply.com/api/") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("clientCode", clientCode)
                append("username", username)
                append("password", password)
                append("request", "verifyUser")
                append("sendContentType", "1")
            }))
        }.body<ErplyResponse<ErplyVerifiedUser>>().contentOrThrow()
    }
}


@Throws(ErplyApiException::class)
internal fun <T> ErplyResponse<T>.contentOrThrow(): T {
    if (status.responseStatus == "ok") {
        return records.firstOrNull() ?: throw ErplyApiException(ErplyApiError.Unknown)
    }
    processErrorCode(status.errorCode)
}

internal fun processErrorCode(errorCode: Int): Nothing {
    throw when (errorCode) {
        // If an authentication request fails with error codes 1051 and 1052, the credentials are wrong (user has been deleted from Erply, or its password updated). Re-sending the call will have no effect!
        1050, 1051, 1052 -> ErplyApiException(ErplyApiError.WrongCredentials)
        //Error codes 1054 and 1055 means that current session has expired. Perform a new verifyUser call with the script's credentials.
        1054, 1055 -> ErplyApiException(ErplyApiError.Unauthorized)
        // Error code 1002 means that the number of requests in the current hour has reached the allowed limit (by default, 1000 requests). The script should save its state and resume work the next hour.
        1002 -> ErplyApiException(ErplyApiError.RequestLimitReached)
        // 1001: Account not found. (Did the user mistype their account number?)
        1001 -> ErplyApiException(ErplyApiError.AccountNotFound)
        /*
            1006: Required module missing on this account, please contact Erply customer support.
            1012: A parameter must have a unique value. (For example: this product code already exists, please pick a new one.)
            1017: Document has been confirmed and its contents and warehouse ID cannot be edited any more.
            1148: USer does not have access to customer data.
            1005: Unknown API call
            1010: Required parameters missing. (The application should have ensured that it got the required input from user.)
            1016: Invalid value
            1020: Bulk API call contained more than 100 sub-requests (max 100 allowed). The whole request has been ignored.
         */
        else -> ErplyApiException(ErplyApiError.Unknown)
    }
}

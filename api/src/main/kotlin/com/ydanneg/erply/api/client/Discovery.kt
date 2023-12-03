package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.model.Endpoints
import com.ydanneg.erply.api.model.ErplyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class DiscoveryApi internal constructor(private val httpClient: HttpClient) {

    suspend fun discoverEndpoints(clientCode: String): Endpoints {
        return httpClient.post("https://$clientCode.erply.com/api/") {
            setBody(FormDataContent(Parameters.build {
                append("clientCode", clientCode)
                append("request", "getServiceEndpoints")
                append("sendContentType", "1")
            }))
        }.body<ErplyResponse<Endpoints>>().contentOrThrow()
    }
}
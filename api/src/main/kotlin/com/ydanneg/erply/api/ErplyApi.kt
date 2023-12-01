package com.ydanneg.erply.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ErplyApi(onLog: (String) -> Unit = {}) {

    val discovery: DiscoveryApi by lazy { DiscoveryApi(httpClient) }
    val auth: AuthApi by lazy { AuthApi(httpClient) }
    val products: ProductApi by lazy { ProductApi(httpClient) }

    private val httpClient = HttpClient(OkHttp.create { config { engineDefaults() } }) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Serializers.json)
        }
        install(UserAgent) {
            agent = "com.ydanneg.erply" // TODO: extract to configuration
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    val msg = if (message.contains("�")) "<binary data>" else message
                    onLog(msg)
                }
            }
            level = LogLevel.ALL // TODO: extract to configuration
        }
    }

    private fun OkHttpClient.Builder.engineDefaults() {
        followRedirects(false)
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)
    }
}
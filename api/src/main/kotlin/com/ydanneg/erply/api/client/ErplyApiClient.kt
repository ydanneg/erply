package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.client.serializer.Serializers
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ErplyApiClient(private val apiConfiguration: ErplyApiClientConfiguration = ErplyApiClientConfiguration()) {

    val auth: AuthApi by lazy { AuthApi(httpClient(null, apiConfiguration.logger)) }
    val products: ProductsApi by lazy { ProductsApi(httpClient(apiConfiguration.baseUrl, apiConfiguration.logger)) }
    val cdn: PicturesApi by lazy { PicturesApi(httpClient(apiConfiguration.cdnBaseUrl, apiConfiguration.logger)) }

    private fun httpClient(baseUrl: String?, logger: LoggerCallback = {}) =
        HttpClient(OkHttp.create { config { engineDefaults() } }) {
            expectSuccess = true
            defaultRequest {
                baseUrl?.also { url(it) }
            }
            install(ContentNegotiation) {
                json(Serializers.json)
            }
            install(UserAgent) {
                agent = apiConfiguration.userAgent
            }
            install(Logging) {
                this.logger = object : Logger {
                    override fun log(message: String) {
                        val msg = if (message.contains("�")) "<binary data>" else message
                        logger(msg)
                    }
                }
                level = apiConfiguration.logLevel.toKtor()
            }
        }

    private fun OkHttpClient.Builder.engineDefaults() {
        followRedirects(false)
        connectTimeout(apiConfiguration.connectionTimeoutSeconds, TimeUnit.SECONDS)
        readTimeout(apiConfiguration.readTimeoutSeconds, TimeUnit.SECONDS)
        writeTimeout(apiConfiguration.writeTimeoutSeconds, TimeUnit.SECONDS)
    }

    private fun ErplyApiClientLogLevel.toKtor() = when (this) {
        ErplyApiClientLogLevel.ALL -> LogLevel.ALL
        ErplyApiClientLogLevel.HEADERS -> LogLevel.HEADERS
        ErplyApiClientLogLevel.BODY -> LogLevel.BODY
        ErplyApiClientLogLevel.INFO -> LogLevel.INFO
        ErplyApiClientLogLevel.NONE -> LogLevel.NONE
    }
}

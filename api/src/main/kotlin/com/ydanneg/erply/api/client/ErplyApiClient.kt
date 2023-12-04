package com.ydanneg.erply.api.client

import com.ydanneg.erply.api.client.serializer.Serializers
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

class ErplyApiClient(private val apiConfiguration: ErplyApiClientConfiguration = ErplyApiClientConfiguration()) {

    val discovery: DiscoveryApi by lazy { DiscoveryApi(httpClient(apiConfiguration.logger)) }
    val auth: AuthApi by lazy { AuthApi(httpClient(apiConfiguration.logger)) }
    val products: ProductsApi by lazy { ProductsApi(httpClient(apiConfiguration.logger)) }

    private fun httpClient(onLog: (String) -> Unit = {}) =
        HttpClient(OkHttp.create { config { engineDefaults() } }) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Serializers.json)
            }
            install(UserAgent) {
                agent = apiConfiguration.userAgent
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        val msg = if (message.contains("�")) "<binary data>" else message
                        onLog(msg)
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

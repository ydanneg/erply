package com.ydanneg.erply.api.client

typealias LoggerCallback = (String) -> Unit

enum class ErplyApiClientLogLevel {
    ALL,
    HEADERS,
    BODY,
    INFO,
    NONE
}

data class ErplyApiClientConfiguration(
    val baseUrl: String = "https://api-pim-eu10.erply.com",
    val logLevel: ErplyApiClientLogLevel = ErplyApiClientLogLevel.NONE,
    val logger: LoggerCallback = {},
    val userAgent: String = "com.ydanneg.erply",
    val connectionTimeoutSeconds: Long = 10,
    val readTimeoutSeconds: Long = 10,
    val writeTimeoutSeconds: Long = 10,
)
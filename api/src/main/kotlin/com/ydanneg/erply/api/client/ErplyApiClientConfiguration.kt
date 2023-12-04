package com.ydanneg.erply.api.client

enum class ErplyApiClientLogLevel {
    ALL,
    HEADERS,
    BODY,
    INFO,
    NONE
}

data class ErplyApiClientConfiguration(
    val logLevel: ErplyApiClientLogLevel = ErplyApiClientLogLevel.NONE,
    val logger: (String) -> Unit = {},
    val userAgent: String = "com.ydanneg.erply",
    val connectionTimeoutSeconds: Long = 10,
    val readTimeoutSeconds: Long = 10,
    val writeTimeoutSeconds: Long = 10,
)
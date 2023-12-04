package com.ydanneg.erply.api.model

enum class ErplyApiError {
    ConnectionError,
    WrongCredentials,
    Unauthorized,
    RequestLimitReached,
    AccountNotFound,
    AccessDenied,
    Unknown
}

class ErplyApiException(val type: ErplyApiError) : Exception() {
    override val message: String
        get() = type.toString()
}
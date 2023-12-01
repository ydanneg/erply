package com.ydanneg.erply.api

enum class ErplyApiError {
    ConnectionError,
    WrongCredentials,
    SessionExpired,
    RequestLimitReached,
    AccountNotFound,
    Unknown
}

class ErplyApiException(val type: ErplyApiError) : Exception()
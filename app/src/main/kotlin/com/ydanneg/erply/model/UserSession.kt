package com.ydanneg.erply.model

data class UserSession(
    val clientCode: String,
    val username: String,
    val userId: String,
    val token: String?,
    val password: String?
)

fun UserSession.isLoggedIn() = token != null
package com.ydanneg.erply.model

data class UserSession(
    val clientCode: String,
    val username: String,
    val userId: String,
    val token: String?
) {
    // password excluded from toString for security reasons
    var password: String? = null

    constructor(clientCode: String, username: String, userId: String, token: String?, password: String?) : this(clientCode, username, userId, token) {
        this.password = password
    }
}

fun UserSession.isLoggedIn() = token != null

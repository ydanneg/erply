package com.ydanneg.erply.datastore.mapper

import com.ydanneg.erply.datastore.UserSessionProto
import com.ydanneg.erply.model.UserSession

fun UserSessionProto.toModel(encryptedPassword: String?): UserSession {
    return UserSession(
        clientCode = clientCode,
        userId = userId,
        username = username,
        token = if (hasToken()) token else null,
        password = encryptedPassword
    )
}

package com.ydanneg.erply.datastore.mapper

import com.google.protobuf.kotlin.toByteString
import com.ydanneg.erply.datastore.DarkThemeConfigProto
import com.ydanneg.erply.datastore.UserPreferencesProto
import com.ydanneg.erply.datastore.UserSessionProto
import com.ydanneg.erply.datastore.UserSessionProto.EncryptedPasswordProto
import com.ydanneg.erply.datastore.copy
import com.ydanneg.erply.model.DarkThemeConfig
import com.ydanneg.erply.model.LastSyncTimestamps
import com.ydanneg.erply.model.UserPreferences
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.security.EncryptedData

fun UserSessionProto.toModel(encryptedPassword: String?): UserSession {
    return UserSession(
        clientCode = clientCode,
        username = username,
        userId = userId,
        token = if (hasToken()) token else null,
        password = encryptedPassword
    )
}


fun UserSession.toProto(proto: UserSessionProto, passwordEncryptedData: EncryptedData?): UserSessionProto {
    val encryptedPassword = EncryptedPasswordProto.getDefaultInstance().let {
        if (passwordEncryptedData != null) {
            it.copy {
                value = passwordEncryptedData.data.toByteString()
                iv = passwordEncryptedData.iv.toByteString()
            }
        } else {
            it
        }
    }
    return proto.copy {
        userId = this@toProto.userId
        username = this@toProto.username
        token = this@toProto.token ?: ""
        clientCode = this@toProto.clientCode
        password = encryptedPassword
    }
}

fun UserPreferencesProto.toModel(): UserPreferences =
    UserPreferences(
        darkThemeConfig = when (darkThemeConfig) {
            DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
            DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
            else -> DarkThemeConfig.FOLLOW_SYSTEM
        },
        isKeepMeSignedIn = keepMeSignedIn,
        lastSyncTimestamps = LastSyncTimestamps(
            productsLastSyncTimestamp = productsLastSyncTimestamp,
            productGroupsLastSyncTimestamp = productGroupsLastSyncTimestamp,
            picturesLastSyncTimestamp = imagesLastSyncTimestamp

        )
    )

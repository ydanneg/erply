package erply.data.datastore.mapper

import com.ydanneg.erply.datastore.UserSessionProto
import erply.model.UserSession

fun UserSessionProto.toModel() = UserSession(
    userId = userId,
    username = username,
    token = token,
    clientCode = clientCode
)
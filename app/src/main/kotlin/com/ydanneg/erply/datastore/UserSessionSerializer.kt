package com.ydanneg.erply.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserSessionSerializer @Inject constructor() : Serializer<UserSessionProto> {
    override val defaultValue: UserSessionProto = UserSessionProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserSessionProto =
        try {
            UserSessionProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserSessionProto, output: OutputStream) {
        t.writeTo(output)
    }
}

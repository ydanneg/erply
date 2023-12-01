@file:UseSerializers(KBigDecimalSerializer::class, KInstantSerializer::class)

package com.ydanneg.erply.model

import com.ydanneg.erply.api.KBigDecimalSerializer
import com.ydanneg.erply.api.KInstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@Serializable
data class ErplyResponseStatus(
    val request: String,
    val responseStatus: String,
    val errorCode: Int,
    val recordsTotal: Long,
    val recordsInResponse: Int
)

@Serializable
data class ErplyVerifiedUser(
    @SerialName("userID")
    val userId: String,
    @SerialName("userName")
    val username: String,
    @SerialName("employeeName")
    val name: String,
    @SerialName("groupName")
    val groupName: String,
    @SerialName("token")
    val token: String
)

@Serializable
data class Endpoints(
    @SerialName("auth")
    val auth: String,
    @SerialName("pim")
    val pim: String
)

@Serializable
class ErplyResponse<R>(
    val status: ErplyResponseStatus,
    val records: List<R>
)

@Serializable
data class ProductName(
    @SerialName("en")
    val en: String
)

@Serializable
data class ErplyProduct(
    @SerialName("id")
    val id: Long,
    @SerialName("type")
    val type: String,
    @SerialName("group_id")
    val groupId: Long,
    @SerialName("name")
    val name: ProductName,
    @SerialName("price")
    val price: String
)
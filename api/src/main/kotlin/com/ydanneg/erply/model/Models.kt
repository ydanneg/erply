@file:UseSerializers(KBigDecimalSerializer::class, KInstantSerializer::class)

package com.ydanneg.erply.model

import com.ydanneg.erply.api.KBigDecimalSerializer
import com.ydanneg.erply.api.KInstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

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
data class LocalizedValue(
    @SerialName("en")
    val en: String = ""
)

enum class ErplyProductType {
    PRODUCT,
    BUNDLE,
    MATRIX,
    ASSEMBLY
}

@Serializable
data class ErplyProduct(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: ErplyProductType = ErplyProductType.PRODUCT,
    @SerialName("group_id")
    val groupId: String,
    @SerialName("name")
    val name: LocalizedValue,
    @SerialName("description")
    val description: LocalizedValue? = null,
    @SerialName("price")
    val price: String,
    @SerialName("changed")
    val changed: Int
)

@Serializable
data class ErplyProductGroup(
    @SerialName("id")
    val id: String,
    @SerialName("parent_id")
    val parentId: String,
    @SerialName("order")
    val order: Int,
    @SerialName("name")
    val name: LocalizedValue,
    @SerialName("description")
    val description: LocalizedValue? = null,
    @SerialName("changed")
    val changed: Int
)
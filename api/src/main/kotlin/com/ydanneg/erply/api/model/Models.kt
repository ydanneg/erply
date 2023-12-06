package com.ydanneg.erply.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

@Serializable
data class LocalizedDescriptionValue(
    @SerialName("en")
    val en: DescriptionValue? = null
)

@Serializable
data class DescriptionValue(
    @SerialName("plain_text")
    val plain: String? = null,
    @SerialName("html")
    val html: String? = null
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
    val description: LocalizedDescriptionValue? = null,
    @SerialName("price")
    val price: String,
    @SerialName("changed")
    val changed: Long
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
    val changed: Long
)

@Serializable
data class ErplyProductPicturesResponse(
    val page: Int,
    val totalRecords: Int,
    val recordsPerPage: Int,
    val recordsReturned: Int,
    val images: List<ErplyProductPicture>
)

@Serializable
data class ErplyProductPicture(
    @SerialName("id")
    val id: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("tenant")
    val tenant: String,
    @SerialName("key")
    val filename: String,
)

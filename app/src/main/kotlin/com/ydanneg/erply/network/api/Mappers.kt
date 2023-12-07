package com.ydanneg.erply.network.api

import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.api.model.ErplyVerifiedUser
import com.ydanneg.erply.model.Product
import com.ydanneg.erply.model.ProductGroup
import com.ydanneg.erply.model.ProductImage
import com.ydanneg.erply.model.UserSession

fun ErplyProductGroup.toModel() = ProductGroup(
    id = id,
    parentId = parentId,
    name = name.en,
    description = description?.en,
    changed = changed,
    order = order
)

fun ErplyProduct.toModel() = Product(
    id = id,
    name = name.en,
    price = price,
    description = description?.en?.let { value -> value.plain?.takeIf { it.isNotBlank() } ?: value.html },
    groupId = groupId,
    changed = changed
)

fun ErplyProductPicture.toModel() = ProductImage(
    id = id,
    productId = productId,
    tenant = tenant,
    filename = filename
)

fun ErplyVerifiedUser.toModel(clientCode: String, password: String? = null) = UserSession(
    username = username,
    userId = userId,
    clientCode = clientCode,
    token = token,
    password = password
)

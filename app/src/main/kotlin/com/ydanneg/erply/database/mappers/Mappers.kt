package com.ydanneg.erply.database.mappers

import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.ErplyProductPicture
import com.ydanneg.erply.api.model.LocalizedValue
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.database.model.ProductImageEntity

fun ErplyProductPicture.toEntity(clientCode: String) = ProductImageEntity(
    id = id,
    clientCode = clientCode,
    productId = productId,
    tenant = tenant,
    filename = filename
)

fun ErplyProduct.toEntity(clientCode: String) = ProductEntity(
    id = id,
    name = name.en,
    type = type,
    groupId = groupId,
    price = price,
    changed = changed,
    description = description?.en,
    clientCode = clientCode
)


fun ProductEntity.fromEntity() = ErplyProduct(
    id = id,
    name = LocalizedValue(name),
    type = type,
    groupId = groupId,
    price = price,
    changed = changed,
    description = description?.let { LocalizedValue(it) }
)


fun ErplyProductGroup.toEntity(clientCode: String) = ProductGroupEntity(
    id = id,
    parentId = parentId,
    name = name.en,
    description = description?.en,
    changed = changed,
    order = order,
    clientCode = clientCode
)

fun ProductGroupEntity.fromEntity() = ErplyProductGroup(
    id = id,
    parentId = parentId,
    name = LocalizedValue(name),
    description = description?.let { LocalizedValue(it) },
    changed = changed,
    order = order
)

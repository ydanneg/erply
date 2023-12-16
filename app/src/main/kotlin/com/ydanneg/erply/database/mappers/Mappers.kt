package com.ydanneg.erply.database.mappers

import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.database.model.ProductImageEntity
import com.ydanneg.erply.model.Product
import com.ydanneg.erply.model.ProductGroup
import com.ydanneg.erply.model.ProductGroupWithProductCount
import com.ydanneg.erply.model.ProductImage

fun ProductImage.toEntity(clientCode: String) = ProductImageEntity(
    id = id,
    clientCode = clientCode,
    productId = productId,
    tenant = tenant,
    filename = filename
)

fun Product.toEntity(clientCode: String) = ProductEntity(
    id = id,
    name = name,
    groupId = groupId,
    price = price,
    changed = changed,
    description = description,
    clientCode = clientCode
)


fun ProductEntity.fromEntity() = Product(
    id = id,
    name = name,
    groupId = groupId,
    price = price,
    changed = changed,
    description = description
)


fun ProductGroup.toEntity(clientCode: String) = ProductGroupEntity(
    id = id,
    parentId = parentId,
    name = name,
    description = description,
    changed = changed,
    order = order,
    clientCode = clientCode
)

fun ProductGroupEntity.fromEntity() = ProductGroup(
    id = id,
    parentId = parentId,
    name = name,
    description = description,
    changed = changed,
    order = order
)


fun ProductGroupWithProductCount.fromWithProductCountEntity() = ProductGroupWithProductCount(
    id = id,
    parentId = parentId,
    name = name,
    description = description,
    changed = changed,
    order = order,
    totalProducts = totalProducts
)

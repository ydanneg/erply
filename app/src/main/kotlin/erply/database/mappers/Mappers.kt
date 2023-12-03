package erply.database.mappers

import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.LocalizedValue
import erply.database.model.ProductEntity
import erply.database.model.ProductGroupEntity

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

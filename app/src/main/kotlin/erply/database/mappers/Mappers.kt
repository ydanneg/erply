package erply.database.mappers

import com.ydanneg.erply.model.ErplyProduct
import com.ydanneg.erply.model.ErplyProductGroup
import com.ydanneg.erply.model.LocalizedValue
import erply.database.model.GroupEntity
import erply.database.model.ProductEntity

fun ErplyProduct.toEntity() = ProductEntity(
    id = id,
    name = name.en,
    type = type,
    groupId = groupId,
    price = price,
    changed = changed,
    description = description?.en
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


fun ErplyProductGroup.toEntity() = GroupEntity(
    id = id,
    parentId = parentId,
    name = name.en,
    description = description?.en,
    changed = changed,
    order = order
)

fun GroupEntity.fromEntity() = ErplyProductGroup(
    id = id,
    parentId = parentId,
    name = LocalizedValue(name),
    description = description?.let { LocalizedValue(it) },
    changed = changed,
    order = order
)
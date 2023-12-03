package erply.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ydanneg.erply.api.model.ErplyProductType

const val PRODUCTS_TABLE_NAME = "products"
const val GROUPS_TABLE_NAME = "product_groups"

@Entity(
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["changed"])
    ],
    tableName = PRODUCTS_TABLE_NAME
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: ErplyProductType = ErplyProductType.PRODUCT,
    val groupId: String,
    val description: String?,
    val price: String,
    val changed: Int
)

@Entity(
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["changed"]),
        Index(value = ["order"])
    ],
    tableName = GROUPS_TABLE_NAME
)
data class ProductGroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val parentId: String,
    val description: String?,
    val changed: Int,
    val order: Int
)
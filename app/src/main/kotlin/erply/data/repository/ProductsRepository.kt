package erply.data.repository

import android.util.Log
import erply.data.api.ErplyApiDataSource
import erply.database.dao.ErplyProductDao
import erply.database.mappers.fromEntity
import erply.database.mappers.toEntity
import erply.database.model.ProductEntity
import erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val erplyApiDataSource: ErplyApiDataSource,
    private val erplyProductDao: ErplyProductDao,
    private val userSessionRepository: UserSessionRepository
) {

    val products = erplyProductDao.getAll().toModel()

    fun productsByGroupId(groupId: String) = erplyProductDao.getAllByGroupId(groupId).toModel()

    fun productsByGroupIdAndNameLike(groupId: String, name: String) = erplyProductDao.findAllByGroupIdAndName(groupId, name).toModel()

    suspend fun loadProductsByGroupId(groupId: String) {
        Log.d(TAG, "Fetching products, group: $groupId")
        val userSession = userSessionRepository.userSessionData.first()
        val products = erplyApiDataSource.fetchProductsByGroupId(userSession.token, groupId).map { it.toEntity() }
        Log.d(TAG, "Received ${products.size} products, group: $groupId")
        erplyProductDao.insertOrIgnore(products)
    }

    private fun Flow<List<ProductEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }

}
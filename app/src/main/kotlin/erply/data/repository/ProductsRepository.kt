package erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
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
    private val erplyApi: ErplyApi,
    private val erplyProductDao: ErplyProductDao,
    private val userSessionRepository: UserSessionRepository
) {

    val products = erplyProductDao.getAll().toModel()

    fun productsByGroupId(groupId: String) = erplyProductDao.getAllByGroupId(groupId).toModel()

    suspend fun loadProductsByGroupId(groupId: String) {
        Log.d(TAG, "Fetching products, group: $groupId")
        val userSession = userSessionRepository.userSessionData.first()
        val received = erplyApi.products.fetchProductsByGroupId(userSession.token, groupId).map { it.toEntity() }
        Log.d(TAG, "Received ${received.size} products, group: $groupId")
        erplyProductDao.insertOrIgnore(received)
    }

    private fun Flow<List<ProductEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }

}
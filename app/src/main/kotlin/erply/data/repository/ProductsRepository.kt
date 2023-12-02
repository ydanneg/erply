package erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
import erply.database.dao.ProductsDao
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
    private val productsDao: ProductsDao,
    private val userSessionRepository: UserSessionRepository
) {

    val products = productsDao.getAll().toModel()

    fun productsByGroupId(groupId: String) = productsDao.getAllByGroupId(groupId).toModel()

    suspend fun loadProductsByGroupId(groupId: String) {
        try {
            val userSession = userSessionRepository.userSessionData.first()
            val received = erplyApi.products.fetchProductsByGroupId(userSession.token, groupId).map { it.toEntity() }
            productsDao.insertOrIgnore(received)
        } catch (e: Throwable) {
            Log.e(TAG, "error", e)
        }
    }

    private fun Flow<List<ProductEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }

}
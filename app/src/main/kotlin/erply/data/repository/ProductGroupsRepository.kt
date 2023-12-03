package erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
import erply.database.dao.ErplyProductGroupDao
import erply.database.mappers.fromEntity
import erply.database.mappers.toEntity
import erply.database.model.ProductGroupEntity
import erply.util.LogUtils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductGroupsRepository @Inject constructor(
    private val erplyApi: ErplyApi,
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository
) {

    val productGroups = erplyProductGroupDao.getAll().toModel()

    fun group(groupId: String) = erplyProductGroupDao.getById(groupId).map { it.fromEntity() }

    suspend fun loadProductGroups() {
        Log.d(TAG, "Fetching product groups...")
        withContext(Dispatchers.IO) {
            val userSession = userSessionRepository.userSessionData.first()
            val received = erplyApi.products.listProductGroups(userSession.token).map { it.toEntity() }
            Log.d(TAG, "Received ${received.size} product groups")
            erplyProductGroupDao.insertOrIgnore(received)
        }

    }

    private fun Flow<List<ProductGroupEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }
}
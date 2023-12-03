package erply.data.repository

import android.util.Log
import erply.data.api.ErplyApiDataSource
import erply.database.dao.ErplyProductGroupDao
import erply.database.mappers.fromEntity
import erply.database.mappers.toEntity
import erply.database.model.ProductGroupEntity
import erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductGroupsRepository @Inject constructor(
    private val erplyApiDataSource: ErplyApiDataSource,
    private val erplyProductGroupDao: ErplyProductGroupDao,
    private val userSessionRepository: UserSessionRepository
) {

    val productGroups = erplyProductGroupDao.getAll().toModel()

    fun group(groupId: String) = erplyProductGroupDao.getById(groupId).map { it.fromEntity() }

    suspend fun loadProductGroups() {
        Log.d(TAG, "Fetching product groups...")
        val userSession = userSessionRepository.userSessionData.first()
        val groups = erplyApiDataSource.listProductGroups(userSession.token).map { it.toEntity() }
        Log.d(TAG, "Received ${groups.size} product groups")
        erplyProductGroupDao.insertOrIgnore(groups)
    }

    private fun Flow<List<ProductGroupEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }
}
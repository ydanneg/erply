package erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
import erply.database.dao.GroupsDao
import erply.database.mappers.fromEntity
import erply.database.mappers.toEntity
import erply.database.model.GroupEntity
import erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductGroupsRepository @Inject constructor(
    private val erplyApi: ErplyApi,
    private val groupsDao: GroupsDao,
    private val userSessionRepository: UserSessionRepository
) {

    val productGroups = groupsDao.getAll().toModel()

    suspend fun loadProductGroups() {
        try {
            val userSession = userSessionRepository.userSessionData.first()
            val received = erplyApi.products.listProductGroups(userSession.token).map { it.toEntity() }
            groupsDao.insertOrIgnore(received)
        } catch (e: Throwable) {
            Log.e(TAG, "error", e)
        }
    }

    private fun Flow<List<GroupEntity>>.toModel() = map { entities -> entities.map { it.fromEntity() } }
}
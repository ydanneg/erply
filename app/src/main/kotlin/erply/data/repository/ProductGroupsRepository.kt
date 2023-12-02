package erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
import com.ydanneg.erply.model.ErplyProductGroup
import erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProductGroupsRepository @Inject constructor(
    private val erplyApi: ErplyApi,
    private val userSessionRepository: UserSessionRepository
) {

    private var _productGroups = MutableStateFlow<List<ErplyProductGroup>>(listOf())
    val productGroups = _productGroups.asStateFlow()

    suspend fun loadProductGroups() {
        try {
            val userSession = userSessionRepository.userSessionData.first()
            val received = erplyApi.products.listProductGroups(userSession.token)
            _productGroups.emit(received)
        } catch (e: Throwable) {
            Log.e(TAG, "error", e)
        }
    }
}
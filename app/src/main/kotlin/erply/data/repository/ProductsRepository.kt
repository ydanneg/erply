package erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
import com.ydanneg.erply.model.ErplyProduct
import erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val erplyApi: ErplyApi,
    private val userSessionRepository: UserSessionRepository
) {

    private var _products = MutableStateFlow<List<ErplyProduct>>(listOf())
    val products = _products.asStateFlow()

    suspend fun loadProducts(groupId: String? = null) {
        try {
            val userSession = userSessionRepository.userSessionData.first()
            val received = erplyApi.products.listProducts(userSession.token, groupId)
            _products.emit(received)
        } catch (e: Throwable) {
            Log.e(TAG, "error", e)
        }
    }
}
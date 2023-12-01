package erply.data.repository

import android.util.Log
import com.ydanneg.erply.api.ErplyApi
import com.ydanneg.erply.model.ErplyProduct
import erply.util.LogUtils.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val erplyApi: ErplyApi,
    private val userSessionRepository: UserSessionRepository
) {

    private var _products = MutableStateFlow<List<ErplyProduct>>(listOf())
    val products = _products.asStateFlow()

    suspend fun loadProducts(groupId: String? = null) {
        val userSession = userSessionRepository.userSessionData.first()
        try {
            val received = erplyApi.products.listProducts(userSession.token)
            _products.emit(received)
        } catch (e: Throwable) {
            Log.e(TAG, "error", e)
        }
    }
}
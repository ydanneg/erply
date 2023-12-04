package com.ydanneg.erply.data.api

import com.ydanneg.erply.api.client.ErplyApiClient
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.ErplyProductGroup
import com.ydanneg.erply.api.model.ErplyVerifiedUser
import com.ydanneg.erply.di.Dispatcher
import com.ydanneg.erply.di.ErplyDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ErplyNetworkDataSource @Inject constructor(
    private val erplyApiClient: ErplyApiClient,
    @Dispatcher(ErplyDispatchers.IO) private val dispatcher: CoroutineDispatcher,
) {

    suspend fun listProductGroups(token: String): List<ErplyProductGroup> = withContext(dispatcher) {
        erplyApiClient.products.listProductGroups(token)
    }

    suspend fun fetchProductGroups(token: String, changedSince: Long? = 0): List<ErplyProductGroup> = withContext(dispatcher) {
        erplyApiClient.products.fetchProductGroups(token, changedSince)
    }

    suspend fun fetchProducts(token: String, changedSince: Long? = 0): List<ErplyProduct> = withContext(dispatcher) {
        erplyApiClient.products.fetchProducts(token, changedSince)
    }

    suspend fun fetchDeletedProductIds(token: String, changedSince: Long? = 0): List<String> = withContext(dispatcher) {
        erplyApiClient.products.fetchDeletedProductIds(token, changedSince)
    }

    suspend fun fetchProductsByGroupId(token: String, groupId: String): List<ErplyProduct> = withContext(dispatcher) {
        erplyApiClient.products.fetchProductsByGroupId(token, groupId)
    }

    suspend fun login(clientCode: String, username: String, password: String): ErplyVerifiedUser = withContext(dispatcher) {
        erplyApiClient.auth.login(clientCode, username, password)
    }
}

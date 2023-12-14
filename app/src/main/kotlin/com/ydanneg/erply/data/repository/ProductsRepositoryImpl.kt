package com.ydanneg.erply.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.ydanneg.erply.database.dao.ErplyProductWithImageDao
import com.ydanneg.erply.model.ProductWithImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PAGE_SIZE = 60

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsRepositoryImpl @Inject constructor(
    private val erplyProductWithImageDao: ErplyProductWithImageDao,
    private val userSessionRepository: UserSessionRepository
) : ProductsRepository {

    override fun getAllProductsByGroupId(groupId: String, sortingOrder: SortingOrder): Flow<PagingData<ProductWithImage>> {
        return userSessionRepository.userSession.map { it.clientCode }.distinctUntilChanged().flatMapLatest { clientCode ->
            Pager(
                PagingConfig(PAGE_SIZE)
            ) {
                getAllByGroupIdOrderedBy(clientCode, groupId, sortingOrder)
            }.flow
        }
    }

    private fun getAllByGroupIdOrderedBy(clientCode: String, groupId: String, order: SortingOrder): PagingSource<Int, ProductWithImage> =
        when (order) {
            SortingOrder.NAME_ASC -> erplyProductWithImageDao.getAllByGroupIdOderByName(clientCode, groupId)
            SortingOrder.NAME_DESC -> erplyProductWithImageDao.getAllByGroupIdOderByName(clientCode, groupId, true)
            SortingOrder.CHANGE_ASC -> erplyProductWithImageDao.getAllByGroupIdOderByChanged(clientCode, groupId)
            SortingOrder.CHANGE_DESC -> erplyProductWithImageDao.getAllByGroupIdOderByChanged(clientCode, groupId, true)
            SortingOrder.PRICE_ASC -> erplyProductWithImageDao.getAllByGroupIdOderByPrice(clientCode, groupId)
            SortingOrder.PRICE_DESC -> erplyProductWithImageDao.getAllByGroupIdOderByPrice(clientCode, groupId, true)
        }

    override fun searchAllProducts(search: String, sortingOrder: SortingOrder): Flow<PagingData<ProductWithImage>> {
        return userSessionRepository.userSession.map { it.clientCode }.distinctUntilChanged().flatMapLatest { clientCode ->
            Pager(
                PagingConfig(PAGE_SIZE)
            ) {
                searchAllOrderedBy(clientCode, "$search*", sortingOrder)
            }.flow
        }
    }

    private fun searchAllOrderedBy(clientCode: String, search: String, order: SortingOrder): PagingSource<Int, ProductWithImage> =
        when (order) {
            SortingOrder.NAME_ASC -> erplyProductWithImageDao.searchAllOderByName(clientCode, search)
            SortingOrder.NAME_DESC -> erplyProductWithImageDao.searchAllOderByName(clientCode, search, true)
            SortingOrder.CHANGE_ASC -> erplyProductWithImageDao.searchAllOderByChanged(clientCode, search)
            SortingOrder.CHANGE_DESC -> erplyProductWithImageDao.searchAllOderByChanged(clientCode, search, true)
            SortingOrder.PRICE_ASC -> erplyProductWithImageDao.searchAllOderByPrice(clientCode, search)
            SortingOrder.PRICE_DESC -> erplyProductWithImageDao.searchAllOderByPrice(clientCode, search, true)
        }
}

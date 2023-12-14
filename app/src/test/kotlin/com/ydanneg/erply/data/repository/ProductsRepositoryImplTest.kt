package com.ydanneg.erply.data.repository

import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.ydanneg.erply.database.dao.ErplyProductWithImageDao
import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.model.ProductWithImage
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.test.testScope
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import kotlin.test.Test

class ProductsRepositoryImplTest {

    private val testScope = testScope()

    @Test
    fun `should provide products from DB filtered by current session clientCode`() = testScope.runTest {
        val groupId = "groupId"
        val networkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        val erplyProductWithImageDao = mockk<ErplyProductWithImageDao>(relaxed = true) {
            coEvery {
                getAllByGroupIdOderByPrice(testSession.clientCode, groupId, true)
            } returns flowOf(testEntities).asPagingSourceFactory(testScope).invoke()
        }
        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true) {
            coEvery { userSession } returns flowOf(testSession)
        }

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )
        val productsRepository = ProductsRepositoryImpl(erplyProductWithImageDao, userSessionRepository)

        val result = productsRepository.getAllProductsByGroupId(groupId, SortingOrder.PRICE_DESC).asSnapshot()
        result shouldBe testEntities

        coVerify(exactly = 1) { erplyProductWithImageDao.getAllByGroupIdOderByPrice(testSession.clientCode, groupId, true) }
        coVerify { userSessionDataSource.userSession }

        confirmVerified(userSessionDataSource, networkDataSource)
    }


    @Test
    fun `should return products from DB filtered by current sessions clientCode and search query`() = testScope.runTest {
        val networkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        val erplyProductWithImageDao = mockk<ErplyProductWithImageDao>(relaxed = true) {
            coEvery {
                searchAllOderByPrice(testSession.clientCode, "searchstring*", true)
            } returns flowOf(testEntities).asPagingSourceFactory(testScope).invoke()
        }
        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true) {
            coEvery { userSession } returns flowOf(testSession)
        }

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )
        val productsRepository = ProductsRepositoryImpl(erplyProductWithImageDao, userSessionRepository)

        val result = productsRepository.searchAllProducts("searchstring", SortingOrder.PRICE_DESC).asSnapshot()
        result shouldBe testEntities

        coVerify(exactly = 1) { erplyProductWithImageDao.searchAllOderByPrice(testSession.clientCode, "searchstring*", true) }
        coVerify { userSessionDataSource.userSession }

        confirmVerified(userSessionDataSource, networkDataSource)
    }

    companion object {
        private val testSession = UserSession(
            clientCode = "clientCode",
            userId = "userId",
            username = "username",
            token = "token",
            password = "password"
        )
        private val testEntities = listOf(
            ProductWithImage(
                id = "id",
                name = "name",
                description = null,
                price = BigDecimal("99.99"),
                filename = "",
                tenant = "tenant"
            )
        )
    }
}

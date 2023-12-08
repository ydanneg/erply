package com.ydanneg.erply.data.repository

import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import com.ydanneg.erply.test.testScope
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ProductsRepositoryImplTest {

    private val testScope = testScope()

    @Test
    fun `should provide products from DB filtered by current session clientCode`() = testScope.runTest {
        val networkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        val erplyProductDao = mockk<ErplyProductDao>(relaxed = true) {
            coEvery { getAll(testSession.clientCode) } returns flowOf(testEntities)
        }
        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true) {
            coEvery { userSession } returns flowOf(testSession)
        }

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )
        val productsRepository = ProductsRepositoryImpl(erplyProductDao, userSessionRepository)

        productsRepository.products.first() shouldBe expectedModels

        coVerify(exactly = 1) { erplyProductDao.getAll(testSession.clientCode) }
        coVerify(exactly = 1) { userSessionDataSource.userSession }

        confirmVerified(erplyProductDao, userSessionDataSource, networkDataSource)
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
            ProductEntity(
                rowId = 0,
                id = "id",
                clientCode = "client",
                name = "name",
                groupId = "groupId",
                description = null,
                price = "99.99",
                changed = 0
            )
        )
        private val expectedModels = testEntities.map { it.fromEntity() }
    }
}

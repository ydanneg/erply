package com.ydanneg.erply.data.repository

import com.ydanneg.erply.database.dao.ErplyProductDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.datastore.UserSessionDataSource
import com.ydanneg.erply.model.UserSession
import com.ydanneg.erply.network.api.ErplyNetworkDataSource
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsRepositoryImplTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun `should provide products from DB filtered by current session clientCode`() = testScope.runTest {
        val entities = listOf(
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
        val models = entities.map { it.fromEntity() }
        val userSession = UserSession(
            clientCode = "clientCode",
            userId = "userId",
            username = "username",
            token = "token",
            password = "password"
        )

        val networkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        val erplyProductDao = mockk<ErplyProductDao>(relaxed = true)
        coEvery { erplyProductDao.getAll(userSession.clientCode) } returns flowOf(entities)

        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true)
        coEvery { userSessionDataSource.userSession } returns flowOf(userSession)

        val userSessionRepository = UserSessionRepositoryImpl(
            erplyNetworkDataSource = networkDataSource,
            userSessionDataSource = userSessionDataSource
        )

        val productsRepository = ProductsRepositoryImpl(erplyProductDao, userSessionRepository)
        productsRepository.products.first() shouldBe models

        coVerify(exactly = 1) { erplyProductDao.getAll(userSession.clientCode) }
        coVerify(exactly = 1) { userSessionDataSource.userSession }

        confirmVerified(erplyProductDao, userSessionDataSource, networkDataSource)
    }
}

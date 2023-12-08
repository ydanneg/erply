package com.ydanneg.erply.data.repository

import com.ydanneg.erply.database.dao.ErplyProductGroupDao
import com.ydanneg.erply.database.mappers.fromEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
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

class ProductGroupsRepositoryImplTest {

    private val scope = testScope()

    private lateinit var productGroupsRepository: ProductGroupsRepository

    @Test
    fun `should return all product groups from DB filtered by current session clientCode`() = scope.runTest {
        val erplyNetworkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        val erplyProductGroupDao = mockk<ErplyProductGroupDao>(relaxed = true) {
            coEvery { getAll(testSession.clientCode) } returns flowOf(testProductGroupEntities)
        }
        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true) {
            coEvery { userSession } returns flowOf(testSession)
        }

        productGroupsRepository = ProductGroupsRepositoryImpl(
            erplyProductGroupDao = erplyProductGroupDao,
            userSessionRepository = UserSessionRepositoryImpl(
                erplyNetworkDataSource = erplyNetworkDataSource,
                userSessionDataSource = userSessionDataSource
            )
        )

        productGroupsRepository.productGroups.first() shouldBe expectedGroupModels

        coVerify { erplyProductGroupDao.getAll(testSession.clientCode) }
        coVerify { userSessionDataSource.userSession }

        confirmVerified(erplyProductGroupDao, erplyNetworkDataSource, userSessionDataSource)
    }

    @Test
    fun `should return single group from DB filtered by current session clientCode and groupId`() = scope.runTest {
        val erplyNetworkDataSource = mockk<ErplyNetworkDataSource>(relaxed = true)
        val groupId = "groupId"
        val erplyProductGroupDao = mockk<ErplyProductGroupDao>(relaxed = true) {
            coEvery { getById(testSession.clientCode, groupId) } returns flowOf(testProductGroupEntities.first())
        }
        val userSessionDataSource = mockk<UserSessionDataSource>(relaxed = true) {
            coEvery { userSession } returns flowOf(testSession)
        }

        productGroupsRepository = ProductGroupsRepositoryImpl(
            erplyProductGroupDao = erplyProductGroupDao,
            userSessionRepository = UserSessionRepositoryImpl(
                erplyNetworkDataSource = erplyNetworkDataSource,
                userSessionDataSource = userSessionDataSource
            )
        )

        productGroupsRepository.group(groupId).first() shouldBe expectedGroupModels.first()

        coVerify { erplyProductGroupDao.getById(testSession.clientCode, groupId) }
        coVerify { userSessionDataSource.userSession }

        confirmVerified(erplyProductGroupDao, erplyNetworkDataSource, userSessionDataSource)
    }

    companion object {
        private val testSession = UserSession(
            clientCode = "clientCode",
            userId = "userId",
            username = "username",
            token = "token",
            password = "password"
        )
        private val testProductGroupEntities = listOf(
            ProductGroupEntity(
                rowId = 0,
                id = "id",
                clientCode = "client",
                name = "name",
                parentId = "parentId",
                description = null,
                changed = 0,
                order = 1
            )
        )
        private val expectedGroupModels = testProductGroupEntities.map { it.fromEntity() }
    }
}

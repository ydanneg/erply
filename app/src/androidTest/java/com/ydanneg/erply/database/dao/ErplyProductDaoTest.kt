package com.ydanneg.erply.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.ydanneg.erply.database.ErplyDatabase
import com.ydanneg.erply.database.model.ProductEntity
import com.ydanneg.erply.database.model.ProductGroupEntity
import com.ydanneg.erply.model.ProductGroupWithProductCount
import io.kotest.matchers.collections.shouldContainAllIgnoringFields
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.math.BigDecimal

class ErplyProductDaoTest {

    private lateinit var db: ErplyDatabase
    private lateinit var productDao: ErplyProductDao
    private lateinit var groupDao: ErplyProductGroupDao

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ErplyDatabase::class.java,
        ).build()
        productDao = db.productDao()
        groupDao = db.groupDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun test() = runTest {
        val clientCode = "clientCode"

        val group = ProductGroupEntity(0, "groupId", clientCode, "name", "0", null, 0, 1)
        val entities = listOf(
            ProductEntity(0, "1", clientCode, "name1", group.id, null, BigDecimal("99.99"), 0),
            ProductEntity(0, "2", clientCode, "name2", group.id, null, BigDecimal("9.99"), 0)
        )

        productDao.insertOrUpdate(entities)
        groupDao.insertOrUpdate(listOf(group))

        groupDao.getAllWithProductCount(clientCode).test {
            awaitItem().apply {
                size shouldBe  1
                this[0] shouldBe ProductGroupWithProductCount(
                    group.id,
                    group.parentId,
                    group.order,
                    group.name,
                    group.description,
                    group.changed,
                    2
                )
            }
        }

        // should all of them by id
        entities.forEach { entity ->
            productDao.getById(entity.clientCode, entity.id).test {
                awaitItem().assertEqualsIgnoringRowId(entity)
            }

        }
        // should not find anything that does not exist (wrong clientCode)
        productDao.getById("wrongClient", "1").test {
            awaitItem() shouldBe null
        }

        // should not find anything that does not exist (wrong id)
        productDao.getById(clientCode, "3").test {
            awaitItem() shouldBe null
        }

        // should return all added entities
        productDao.getAll(clientCode).test {
            awaitItem().shouldContainAllIgnoringFields(entities, ProductEntity::rowId)
        }

        productDao.delete(clientCode, entities.map { it.id })

        // should return nothing after deletion
        productDao.getAll(clientCode).test {
            awaitItem() shouldBe listOf()
        }
    }

    private fun ProductEntity?.assertEqualsIgnoringRowId(expected: ProductEntity) {
        this shouldNotBe null
        this!! shouldBeEqualUsingFields {
            excludedProperties = setOf(ProductEntity::rowId)
            expected
        }
    }
}

package com.ydanneg.erply.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.ydanneg.erply.database.ErplyDatabase
import com.ydanneg.erply.database.model.ProductEntity
import io.kotest.matchers.collections.shouldContainAllIgnoringFields
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ErplyProductDaoTest {

    private lateinit var db: ErplyDatabase
    private lateinit var productDao: ErplyProductDao

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ErplyDatabase::class.java,
        ).build()
        productDao = db.productDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun test() = runTest {
        val clientCode = "clientCode"

        val entities = listOf(
            ProductEntity(0, "1", clientCode, "name1", "groupId", null, "99.99", 0),
            ProductEntity(0, "2", clientCode, "name2", "groupId", null, "9.99", 0)
        )

        productDao.insertOrUpdate(entities)

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

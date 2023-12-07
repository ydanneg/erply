package com.ydanneg.erply.api.client.util

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ErplyApiFilterTest {

    @Test
    fun testAnd() {
        apiFilter {
            and {
                eq("a", "1")
                eq("b", "2")
            }
        } shouldBe """[["a","=","1"],"and",["b","=","2"]]"""
    }

    @Test
    fun testOr() {
        apiFilter {
            or {
                eq("a", "1")
                not("b", "2")
            }
        } shouldBe """[["a","=","1"],"or",["b","!=","2"]]"""
    }

    @Test
    fun testNested() {
        apiFilter {
            or {
                and {
                    eq("status", "ACTIVE")
                    gte("group_id", "2")
                }
                `in`("id", listOf(1, 2, 3))
            }
        } shouldBe """[[["status","=","ACTIVE"],"and",["group_id",">=","2"]],"or",["id","in",[1,2,3]]]"""
    }

    @Test
    fun testEq() {
        val expectedResult = """[["id","=","1"]]"""
        apiFilter { eq("id", 1) } shouldBe expectedResult
        apiFilter { eq("id", "1") } shouldBe expectedResult
        apiFilter { eq("id", 1L) } shouldBe expectedResult
    }

    @Test
    fun testNot() {
        val expectedResult = """[["id","!=","1"]]"""
        apiFilter { not("id", 1) } shouldBe expectedResult
        apiFilter { not("id", "1") } shouldBe expectedResult
        apiFilter { not("id", 1L) } shouldBe expectedResult
    }

    @Test
    fun testLte() {
        val expectedResult = """[["id","<=","1"]]"""
        apiFilter { lte("id", 1) } shouldBe expectedResult
        apiFilter { lte("id", "1") } shouldBe expectedResult
        apiFilter { lte("id", 1L) } shouldBe expectedResult
    }


    @Test
    fun testContains() {
        val expectedResult = """[["id","contains","1"]]"""
        apiFilter { contains("id", 1) } shouldBe expectedResult
        apiFilter { contains("id", "1") } shouldBe expectedResult
        apiFilter { contains("id", 1L) } shouldBe expectedResult
    }

    @Test
    fun testStartsWith() {
        val expectedResult = """[["id","startswith","1"]]"""
        apiFilter { startsWith("id", 1) } shouldBe expectedResult
        apiFilter { startsWith("id", "1") } shouldBe expectedResult
        apiFilter { startsWith("id", 1L) } shouldBe expectedResult
    }

    @Test
    fun testGte() {
        val expectedResult = """[["id",">=","1"]]"""
        apiFilter { gte("id", 1) } shouldBe expectedResult
        apiFilter { gte("id", "1") } shouldBe expectedResult
        apiFilter { gte("id", 1L) } shouldBe expectedResult
    }

    @Test
    fun testIn() {
        val expectedResult = """[["id","in",[1,2,3]]]"""
        apiFilter { `in`("id", listOf(1, 2, 3)) } shouldBe expectedResult
        apiFilter { `in`("id", arrayOf(1, 2, 3)) } shouldBe expectedResult
        apiFilter { `in`("id", setOf(1, 2, 3)) } shouldBe expectedResult
        apiFilter { `in`("id", arrayOf("1", "2", "3")) } shouldBe expectedResult
    }

    @Test
    fun testNotIn() {
        val expectedResult = """[["id","not in",[1,2,3]]]"""
        apiFilter { notIn("id", listOf(1, 2, 3)) } shouldBe expectedResult
        apiFilter { notIn("id", arrayOf(1, 2, 3)) } shouldBe expectedResult
        apiFilter { notIn("id", setOf(1, 2, 3)) } shouldBe expectedResult
        apiFilter { notIn("id", arrayOf("1", "2", "3")) } shouldBe expectedResult
    }

    @Test
    fun testMultipleOneLevel() {
        val expected = """[["displayed_in_webshop","=","1"],"and",["status","=","ACTIVE"],"and",["changed",">=","1701935870"]]"""
        apiFilter {
            eq("displayed_in_webshop", 1)
            eq("status", "ACTIVE")
            gte("changed", 1701935870)
        } shouldBe expected
    }

}

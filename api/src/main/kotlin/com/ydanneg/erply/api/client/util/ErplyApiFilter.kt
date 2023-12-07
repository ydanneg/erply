@file:Suppress("HardCodedStringLiteral")

package com.ydanneg.erply.api.client.util


internal class ErplyApiFilter(private val operand: String) {
    private val conditions = mutableListOf<String>()

    private fun append(condition: ErplyApiFilter) {
        conditions.add(condition.toString())
    }

    private fun append(name: String, operand: String, value: Any) {
        conditions.add("[\"$name\",\"$operand\",\"$value\"]")
    }

    private fun <T> appendArray(name: String, operand: String, value: Array<out T>) {
        conditions.add("[\"$name\",\"$operand\",[${value.joinToString(",")}]]")
    }

    private fun appendCollection(name: String, operand: String, value: Collection<Any>) {
        conditions.add("[\"$name\",\"$operand\",[${value.joinToString(",")}]]")
    }

    override fun toString(): String =
        if (conditions.size == 1) {
            conditions.first().toString()
        } else {
            conditions.joinToString(prefix = "[", postfix = "]", separator = ",\"${operand}\",") { it }
        }

    fun ErplyApiFilter.and(block: ErplyApiFilter.() -> Unit): ErplyApiFilter {
        val condition = ErplyApiFilter("and").apply(block)
        append(condition)
        return condition
    }

    fun ErplyApiFilter.or(block: ErplyApiFilter.() -> Unit): ErplyApiFilter {
        val condition = ErplyApiFilter("or").apply(block)
        append(condition)
        return condition
    }


    fun ErplyApiFilter.eq(name: String, value: Any) {
        append(name, "=", value)
    }

    fun ErplyApiFilter.not(name: String, value: Any) {
        append(name, "!=", value)
    }

    fun ErplyApiFilter.gte(name: String, value: Any) {
        append(name, ">=", value)
    }

    fun ErplyApiFilter.lte(name: String, value: Any) {
        append(name, "<=", value)
    }

    fun ErplyApiFilter.`in`(name: String, value: Array<Any>) {
        appendArray(name, "in", value)
    }

    fun ErplyApiFilter.`in`(name: String, value: Collection<Any>) {
        appendCollection(name, "in", value)
    }

    fun ErplyApiFilter.notIn(name: String, value: Array<Any>) {
        appendArray(name, "not in", value)
    }

    fun ErplyApiFilter.notIn(name: String, value: Collection<Any>) {
        appendCollection(name, "not in", value)
    }

    fun ErplyApiFilter.contains(name: String, value: Any) {
        append(name, "contains", value)
    }

    fun ErplyApiFilter.startsWith(name: String, value: Any) {
        append(name, "startswith", value)
    }
}

internal fun apiFilter(block: ErplyApiFilter.() -> Unit): String {
    val filter = ErplyApiFilter("and").apply(block).toString()
    // The single filter must be still an array of arrays.
    // This is a hack
    if (filter.commonPrefixWith("[[").length == 1) {
        return "[$filter]"
    }
    return filter
}

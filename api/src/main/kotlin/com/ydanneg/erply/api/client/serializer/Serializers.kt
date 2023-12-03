package com.ydanneg.erply.api.client.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

@OptIn(ExperimentalSerializationApi::class)
internal object Serializers {
    val json = buildWithDefaults()
    val prettyJson = buildWithDefaults { prettyPrint = true }

    private fun buildWithDefaults(builderBlock: JsonBuilder.() -> Unit = {}): Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
        this.builderBlock()
    }
}

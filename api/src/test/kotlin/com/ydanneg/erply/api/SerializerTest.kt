package com.ydanneg.erply.api

import com.ydanneg.erply.api.client.serializer.Serializers
import com.ydanneg.erply.api.model.DescriptionValue
import com.ydanneg.erply.api.model.ErplyProduct
import com.ydanneg.erply.api.model.LocalizedDescriptionValue
import com.ydanneg.erply.api.model.LocalizedValue
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.math.BigDecimal

class SerializerTest {

    @Test
    fun `should deserialize Erply Product from json`() {

        val json = """
{
  "changed": 1643796324,
  "description": {
    "en": {
      "plain_text": "",
      "html": ""
    },
    "ru": {
      "plain_text": "",
      "html": ""
    },
    "fi": {
      "plain_text": "",
      "html": ""
    }
  },
  "group_id": 50,
  "id": 32646,
  "name": {
    "en": "Big Foot 3G Cellular Game Camera  Pre-Loaded AT\u0026T Sim Cards 12mp 0.4s Trigger Sp"
  },
  "price": 169.99,
  "type": "PRODUCT"
}
        """.trimIndent()

        val expected = ErplyProduct(
            "32646",
            "50",
            LocalizedValue("Big Foot 3G Cellular Game Camera  Pre-Loaded AT\u0026T Sim Cards 12mp 0.4s Trigger Sp"),
            LocalizedDescriptionValue(en = DescriptionValue("", "")),
            price = BigDecimal("169.99"),
            changed = 1643796324
        )
        val product = Serializers.json.decodeFromString<ErplyProduct>(json)

        product shouldBe expected
    }
}

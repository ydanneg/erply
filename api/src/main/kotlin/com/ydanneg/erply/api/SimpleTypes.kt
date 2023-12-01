@file:Suppress("EXPERIMENTAL_API_USAGE", "EXTERNAL_SERIALIZER_USELESS")

package com.ydanneg.erply.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Instant::class)
object KInstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(DateTimeFormatter.ISO_INSTANT.format(value))
    }

    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}

@Serializer(forClass = LocalDate::class)
object KLocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(DateTimeFormatter.ISO_LOCAL_DATE.format(value))
    }

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

@Serializer(forClass = BigDecimal::class)
object KBigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeLong(value.toLong())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeLong())
    }
}
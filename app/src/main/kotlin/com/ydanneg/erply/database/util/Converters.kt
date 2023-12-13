package com.ydanneg.erply.database.util

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalConverter {
    @TypeConverter
    fun bigDecimalToDouble(value: BigDecimal?): Double? =
        value?.toDouble()

    @TypeConverter
    fun doubleToBigDecimal(instant: Double?): BigDecimal? =
        instant?.toBigDecimal()?.setScale(2, RoundingMode.HALF_EVEN)
}

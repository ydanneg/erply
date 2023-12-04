package com.ydanneg.erply.ui.util

import kotlin.random.Random

private val alphaNumericCharPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + ' '

fun generateAlphanumeric(length: Int = Random.nextInt(3, 100)): String =
    (1..length).map { alphaNumericCharPool.random() }.joinToString("")
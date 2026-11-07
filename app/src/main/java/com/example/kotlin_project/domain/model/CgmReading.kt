package com.example.kotlin_project.domain.model

import java.util.concurrent.TimeUnit

data class CgmReading(
    val glucoseMmol: Double,
    val timestampMillis: Long
) {

    fun ageMinutes(nowMillis: Long): Long =
        TimeUnit.MILLISECONDS.toMinutes((nowMillis - timestampMillis).coerceAtLeast(0L))

    fun isFresh(nowMillis: Long): Boolean = ageMinutes(nowMillis) <= MAX_AGE_MINUTES

    companion object {

        const val MAX_AGE_MINUTES = 15L
    }
}
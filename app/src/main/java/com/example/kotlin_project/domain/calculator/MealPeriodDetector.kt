package com.example.kotlin_project.domain.calculator

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object MealPeriodDetector {

    const val DEFAULT_PERIOD = "Lunch"

    private const val MINUTES_PER_DAY = 24 * 60

    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm")

    fun detect(mealTimes: Map<String, Pair<String, String>>, time: LocalTime): String {
        val minute = time.toSecondOfDay() / 60
        val ranges = mealTimes.mapNotNull { (period, range) ->
            val start = parseTime(range.first) ?: return@mapNotNull null
            val end = parseTime(range.second) ?: return@mapNotNull null
            MealRange(period, start.toSecondOfDay() / 60, end.toSecondOfDay() / 60)
        }
        val current = ranges.firstOrNull { it.contains(minute) }
        val nearest = ranges.minWithOrNull(
            compareBy<MealRange> { it.distanceTo(minute) }.thenBy { it.minutesUntilStart(minute) }
        )
        return (current ?: nearest)?.period ?: DEFAULT_PERIOD
    }

    fun parseTime(text: String): LocalTime? =
        try {
            LocalTime.parse(text.trim(), TIME_FORMATTER)
        } catch (e: DateTimeParseException) {
            null
        }

    private class MealRange(val period: String, val start: Int, val end: Int) {

        fun contains(minute: Int): Boolean =
            if (start <= end) minute in start..end else minute >= start || minute <= end

        fun minutesUntilStart(minute: Int): Int = (start - minute).mod(MINUTES_PER_DAY)

        fun distanceTo(minute: Int): Int =
            minOf(minutesUntilStart(minute), (minute - end).mod(MINUTES_PER_DAY))
    }
}
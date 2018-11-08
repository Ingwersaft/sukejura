package io.kesselring.sukejura.pattern

import io.kesselring.sukejura.Dsl
import java.time.LocalDateTime
import java.time.Month
import java.time.Year

@Dsl
sealed class DaysOfMonth {
    /**
     * @param value 1-31 possible
     */
    data class D(val value: Int) : DaysOfMonth() {
        init {
            if (IntRange(1, 31).contains(value).not()) {
                throw IllegalArgumentException("given $value not between 1 and 31")
            }
        }
    }

    object Last : DaysOfMonth()
    object Every : DaysOfMonth()
}

fun Set<DaysOfMonth>.isActive(currentTime: LocalDateTime): Boolean {
    // this should handle an empty set aswell
    val matches = map {
        when (it) {
            is DaysOfMonth.Every -> true
            is DaysOfMonth.D -> currentTime.dayOfMonth == it.value
            is DaysOfMonth.Last -> {
                when {
                    Year.isLeap(currentTime.year.toLong()) && currentTime.month.value == Month.FEBRUARY.value -> {
                        currentTime.dayOfMonth == 29
                    }
                    else -> currentTime.dayOfMonth == 28
                }
            }
        }
    }
    return if (matches.isEmpty()) {
        true
    } else {
        matches.any { it }
    }
}
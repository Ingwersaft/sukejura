package io.kesselring.kron.pattern

import java.time.LocalDateTime

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
    val anyFailed = map {
        when (it) {
            is DaysOfMonth.Every -> true
            is DaysOfMonth.D -> currentTime.dayOfMonth == it.value
            is DaysOfMonth.Last -> currentTime.dayOfMonth == currentTime.month.maxLength()
        }
    }.any { !it }
    return anyFailed.not()
}
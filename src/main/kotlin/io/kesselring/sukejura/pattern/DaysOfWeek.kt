package io.kesselring.sukejura.pattern

import io.kesselring.sukejura.Dsl
import java.time.DayOfWeek

@Dsl
sealed class DaysOfWeek {
    object Every : DaysOfWeek()
    object Mon : DaysOfWeek()
    object Tue : DaysOfWeek()
    object Wed : DaysOfWeek()
    object Thu : DaysOfWeek()
    object Fri : DaysOfWeek()
    object Sat : DaysOfWeek()
    object Sun : DaysOfWeek()
}

fun Set<DaysOfWeek>.isActive(dayOfWeek: DayOfWeek): Boolean {
    val matches = map {
        when (it) {
            is DaysOfWeek.Every -> true
            DaysOfWeek.Mon -> dayOfWeek == DayOfWeek.MONDAY
            DaysOfWeek.Tue -> dayOfWeek == DayOfWeek.TUESDAY
            DaysOfWeek.Wed -> dayOfWeek == DayOfWeek.WEDNESDAY
            DaysOfWeek.Thu -> dayOfWeek == DayOfWeek.THURSDAY
            DaysOfWeek.Fri -> dayOfWeek == DayOfWeek.FRIDAY
            DaysOfWeek.Sat -> dayOfWeek == DayOfWeek.SATURDAY
            DaysOfWeek.Sun -> dayOfWeek == DayOfWeek.SUNDAY
        }
    }
    return if (matches.isEmpty()) {
        true
    } else {
        matches.any { it }
    }
}
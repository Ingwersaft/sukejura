package io.kesselring.kron

import io.kesselring.kron.pattern.DaysOfMonth
import io.kesselring.kron.pattern.Hours
import io.kesselring.kron.pattern.Minutes
import io.kesselring.kron.pattern.MonthsOfYear
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class KronTest {
    @Test
    fun testLeapYears() {
        val kron = kron {
            minute {
                // only on minute 11
                Minutes.M(11)
            }
            dayOfMonth { DaysOfMonth.Last } // only on the last day of the month
            monthOfYear { MonthsOfYear.Feb } // only in february
            hour { Hours.H(15) } // only on the 15th hour

            task { println("running something") }
        }
        val invocations = kron.invocations().take(5).toSet()
        val expectedTime = LocalTime.of(15, 11)
        assertEquals(
            invocations, setOf(
                LocalDateTime.of(LocalDate.of(2019, 2, 28), expectedTime),
                LocalDateTime.of(LocalDate.of(2020, 2, 29), expectedTime),
                LocalDateTime.of(LocalDate.of(2021, 2, 28), expectedTime),
                LocalDateTime.of(LocalDate.of(2022, 2, 28), expectedTime),
                LocalDateTime.of(LocalDate.of(2023, 2, 28), expectedTime)
            )
        )
    }
}
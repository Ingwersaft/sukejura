package io.kesselring.kron

import io.kesselring.kron.pattern.DaysOfMonth
import io.kesselring.kron.pattern.isActive
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DaysOfMonthTest {
    @Test
    fun testLeapYears() {
        setOf(DaysOfMonth.Last).isActive(LocalDateTime.of(LocalDate.of(2019, 2, 28), LocalTime.of(0, 0))) assert true
        setOf(DaysOfMonth.Last).isActive(LocalDateTime.of(LocalDate.of(2020, 2, 28), LocalTime.of(0, 0))) assert false
        setOf(DaysOfMonth.Last).isActive(LocalDateTime.of(LocalDate.of(2020, 2, 29), LocalTime.of(0, 0))) assert true
    }
}
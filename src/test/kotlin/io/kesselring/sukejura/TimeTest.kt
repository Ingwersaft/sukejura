package io.kesselring.sukejura

import io.kesselring.sukejura.pattern.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeTest {
    @Test
    fun testMinuteIsActive() {
        val givenTime = LocalDateTime.parse("2011-12-03T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        println("checking with $givenTime")
        mutableSetOf<Minutes>(
            Minutes.Every
        ).isActive(givenTime.minute) assert true
        mutableSetOf<Minutes>(
            Minutes.M(0)
        ).isActive(0) assert true
    }

    @Test
    fun testHourIsActive() {
        val givenTime = LocalDateTime.parse("2011-12-03T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        println("checking with $givenTime")
        mutableSetOf<Hours>(
            Hours.H(10)
        ).isActive(givenTime.hour) assert true
        mutableSetOf<Hours>(
            Hours.H(2)
        ).isActive(0) assert false
    }

    @Test
    fun testDaysOfMonth() {
        val d29 = febDay("29", "2016") // 2016 == leap year
        setOf<DaysOfMonth>(DaysOfMonth.Last).isActive(d29) assert true
        setOf<DaysOfMonth>(DaysOfMonth.D(29)).isActive(d29) assert true
        setOf<DaysOfMonth>(DaysOfMonth.D(28)).isActive(d29) assert false
        setOf<DaysOfMonth>(DaysOfMonth.Every).isActive(d29) assert true
        setOf<DaysOfMonth>().isActive(d29) assert true // empty == every aka default

        setOf<DaysOfMonth>().isActive(febDay("22", "2018")) assert true

        setOf<DaysOfMonth>().isActive(LocalDateTime.of(LocalDate.of(2019, 2, 28), LocalTime.of(15, 11))) assert true
    }

    private fun febDay(d: String, y: String = "2011") =
        LocalDateTime.parse("$y-02-${d}T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @Test
    fun testMonthsOfYear() {
        setOf<MonthsOfYear>().isActive(1) assert true
        setOf<MonthsOfYear>(
            MonthsOfYear.Every
        ).isActive(1) assert true
        setOf<MonthsOfYear>(
            MonthsOfYear.Jan
        ).isActive(2) assert false

        setOf<MonthsOfYear>(
            MonthsOfYear.Jan
        ).isActive(2) assert false
        setOf(
            MonthsOfYear.Feb,
            MonthsOfYear.Jan
        ).isActive(2) assert true
    }

    @Test
    fun testDaysOfWeek() {
        setOf<DaysOfWeek>().isActive(DayOfWeek.MONDAY) assert true
        setOf<DaysOfWeek>(DaysOfWeek.Thu).isActive(DayOfWeek.MONDAY) assert false
        setOf(DaysOfWeek.Thu, DaysOfWeek.Wed).isActive(DayOfWeek.MONDAY) assert false
        setOf(DaysOfWeek.Thu, DaysOfWeek.Wed).isActive(DayOfWeek.WEDNESDAY) assert true
    }
}

infix fun Boolean.assert(b: Boolean) {
    assertTrue(this == b)
}

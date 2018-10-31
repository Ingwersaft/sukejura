package io.kesselring.kron

import io.kesselring.kron.pattern.Minutes
import io.kesselring.kron.pattern.isActive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MinutesTest {
    @Test
    fun testM() {
        assertThrows<IllegalArgumentException> { Minutes.M(-1) }
        assertThrows<IllegalArgumentException> { Minutes.M(60) }
    }

    @Test
    fun testMultipleMinutes() {
        var givenTime = LocalDateTime.parse("2011-12-03T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .truncatedTo(ChronoUnit.MINUTES)
        println("checking with $givenTime")

        val underTest = mutableSetOf<Minutes>(
            Minutes.M(10),
            Minutes.M(11)
        )
        while (true) {
            println(givenTime)
            println(underTest.isActive(givenTime.minute))
            println()
            givenTime = givenTime.plusMinutes(1)
        }
    }
}
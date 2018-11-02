package io.kesselring.sukejura

import io.kesselring.sukejura.pattern.DaysOfMonth
import io.kesselring.sukejura.pattern.Hours
import io.kesselring.sukejura.pattern.Minutes
import io.kesselring.sukejura.pattern.MonthsOfYear
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.concurrent.atomic.AtomicInteger

class SukejuraTest {
    @Test
    fun testLeapYears() {
        val sukejura = sukejura {
            minute {
                // only on minute 11
                Minutes.M(11)
            }
            dayOfMonth { DaysOfMonth.Last } // only on the last day of the month
            monthOfYear { MonthsOfYear.Feb } // only in february
            hour { Hours.H(15) } // only on the 15th hour

            task { println("running something") }
        }
        val invocations = sukejura.invocations().take(5).toSet()
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

    @Test
    fun testTaskExection() = runBlocking {
        var executions = AtomicInteger()
        val date = LocalDate.of(2018, 1, 1)
        val times = mutableListOf(
            // will trigger cause every min means the first min -> executions = 1
            LocalDateTime.of(
                date,
                LocalTime.of(11, 1, 20)
            ),
            // same minute, so no execution
            LocalDateTime.of(date, LocalTime.of(11, 1, 40)),
            // next minute, so another execution -> executions = 2
            LocalDateTime.of(date, LocalTime.of(11, 2, 1)),
            // same minute, so no execution
            LocalDateTime.of(date, LocalTime.of(11, 2, 50)),
            // next minute, so another execution -> executions = 3
            LocalDateTime.of(date, LocalTime.of(11, 3, 30))
        )
        val testClock = TestClock(
            times
        )
        val sukejura = sukejura {
            clock = testClock
            minute {
                Minutes.Every
            }
            task {
                println("task()")
                executions.incrementAndGet()
            }
            start()
        }
        val current = System.currentTimeMillis()
        while (executions.get() < 3) {
            Thread.sleep(1)
            if (System.currentTimeMillis() - current > 5000) {
                throw IllegalStateException("aborting after 5s")
            }
        }
        sukejura.stop()
        assertEquals(3, executions.get(), "wrong amount of executions")
        assertEquals(0, times.size, "all times consumed")
    }
}

class TestClock(val times: MutableList<LocalDateTime>) : Clock() {
    override fun withZone(zone: ZoneId?): Clock {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getZone(): ZoneId {
        return ZoneId.systemDefault()
    }

    override fun instant(): Instant {
        return times.removeAt(0).truncatedTo(ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant()
    }
}
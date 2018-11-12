package io.kesselring.sukejura

import io.kesselring.sukejura.pattern.DaysOfMonth
import io.kesselring.sukejura.pattern.Hours
import io.kesselring.sukejura.pattern.Minutes
import io.kesselring.sukejura.pattern.MonthsOfYear
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MonthsOfYearTest {
    @Test
    fun testMonthly() {
        val sukejura = sukejura {
            schedule {
                monthsOfYear {
                    listOf(
                        MonthsOfYear.Jan,
                        MonthsOfYear.Feb,
                        MonthsOfYear.Mar,
                        MonthsOfYear.Apr,
                        MonthsOfYear.May,
                        MonthsOfYear.Jun,
                        MonthsOfYear.Jul,
                        MonthsOfYear.Aug,
                        MonthsOfYear.Sep,
                        MonthsOfYear.Oct,
                        MonthsOfYear.Nov,
                        MonthsOfYear.Dec
                    )
                }
                minute { Minutes.M(0) }
                hour { Hours.H(0) }
                dayOfMonth { DaysOfMonth.D(1) }
            }
        }
        val invocationDays = sukejura.schedules.first().invocations().take(12).map { it.toLocalDate() }.toList()

        val nowWithDayOne = LocalDate.now().withDayOfMonth(1)
        val firstDay = if (nowWithDayOne == LocalDate.now()) {
            nowWithDayOne
        } else {
            nowWithDayOne.plusMonths(1)
        }
        //
        val nextCalculatedInvocations = (0..11).map { firstDay.plusMonths(it.toLong()) }
        assertEquals(invocationDays, nextCalculatedInvocations)
    }
}
package io.kesselring.sukejura.examples

import io.kesselring.sukejura.pattern.DaysOfMonth
import io.kesselring.sukejura.pattern.Hours
import io.kesselring.sukejura.pattern.Minutes
import io.kesselring.sukejura.pattern.MonthsOfYear
import io.kesselring.sukejura.sukejura

fun main(args: Array<String>) {
    sukejura {
        monthOfYear { MonthsOfYear.Feb }
        dayOfMonth {
            DaysOfMonth.D(29)
        }
        minute { Minutes.M(0) }
        hour { Hours.H(0) }

        task {
            println("hi")
        }
        start()
        invocations().take(10).forEach {
            println("leap year at 0:0: $it")
        }
    }
}
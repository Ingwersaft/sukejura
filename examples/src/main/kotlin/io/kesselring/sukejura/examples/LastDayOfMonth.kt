package io.kesselring.sukejura.examples

import io.kesselring.sukejura.pattern.DaysOfMonth
import io.kesselring.sukejura.sukejura

fun main(args: Array<String>) {
    sukejura {
        schedule {
            dayOfMonth { DaysOfMonth.Last }
            task {
                println("byebye month")
            }
            invocations().take(10).forEach {
                println(it)
            }
        }
        start()
    }
}
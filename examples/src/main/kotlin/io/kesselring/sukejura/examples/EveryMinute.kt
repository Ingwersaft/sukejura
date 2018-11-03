package io.kesselring.sukejura.examples

import io.kesselring.sukejura.sukejura

fun main(args: Array<String>) {
    sukejura {
        task {
            println("default behaviour is every minute")
        }
        start()
    }
}
package io.kesselring.kron

import io.kesselring.kron.pattern.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

fun kron(block: Kron.() -> Unit): Kron {
    val kron = Kron()
    kron.block()
    return kron
}

class Kron : CoroutineScope {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatcher//newSingleThreadContext("kron-context")

    private val minutes: MutableSet<Minutes> = mutableSetOf()
    private val hours: MutableSet<Hours> = mutableSetOf()
    private val daysOfMonth: MutableSet<DaysOfMonth> = mutableSetOf()
    private val monthsOfYear: MutableSet<MonthsOfYear> = mutableSetOf()
    private val daysOfWeek: MutableSet<DaysOfWeek> = mutableSetOf()

    private var exec: suspend (LocalDateTime) -> Unit = { throw IllegalStateException("no task block has been setup") }

    fun minute(block: () -> Minutes) {
        minutes.add(block())
    }

    fun minutes(block: () -> List<Minutes>) {
        minutes.addAll(block())
    }

    fun task(block: suspend (LocalDateTime) -> Unit) {
        exec = block
    }

    internal var started = false
    fun start(): Kron {
        launch(
            context = dispatcher
            //newSingleThreadContext("kron-background-timer")
        ) {
            var lastCheck: LocalDateTime? = null
            while (true) {
                val nowMinute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                if (lastCheck == nowMinute) {
                    // already handled $nowMinute
                } else {
                    lastCheck = nowMinute
                    println("new minute $nowMinute, going to check if we should execute now")
                    if (active(nowMinute)) {
                        exec(nowMinute)
                    }
                }
                delay(50)
            }
        }
        started = true
        println("launched")
        return this
    }

    internal fun active(nowMinute: LocalDateTime): Boolean = nowMinute.let {
        val minutesActive = minutes.isActive(it.minute)
        val hoursActive = hours.isActive(it.minute)
        val monthsOfYearActive = monthsOfYear.isActive(it.monthValue)
        val daysOfMonthActive = daysOfMonth.isActive(it)
        val daysOfWeekActive = daysOfWeek.isActive(it.dayOfWeek)
        listOf(minutesActive, hoursActive, monthsOfYearActive, daysOfMonthActive, daysOfWeekActive)
            .all {
                true
            }
    }

    fun stop() {
        job.cancel()
    }
}


fun Kron.getNextInvocations(amount: Int): List<LocalDateTime> {
    if (started.not()) throw IllegalStateException("not started yet")
    TODO()
}

fun main(args: Array<String>) {
    System.setProperty(DEBUG_PROPERTY_NAME, "on")
    val kron = kron {
        minute { Minutes.Every }
        minutes {
            listOf(
                Minutes.M(10),
                Minutes.M(11)
            )
        }
        task { println("running something") }
    }.start()
    println("now going to sleep 3 minutes")
    Thread.sleep(TimeUnit.MINUTES.toMillis(2))
    println("stopping kron")
    kron.stop()

    Thread.sleep(TimeUnit.MINUTES.toMillis(2))
    println("hopefully kron stopped")
}
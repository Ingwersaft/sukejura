package io.kesselring.sukejura

import io.kesselring.sukejura.pattern.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@Dsl
fun sukejura(block: Sukejura.() -> Unit): Sukejura {
    val sukejura = Sukejura()
    sukejura.block()
    return sukejura
}

@Dsl
class Sukejura : CoroutineScope {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatcher//newSingleThreadContext("sukejura-context")

    private val minutes: MutableSet<Minutes> = mutableSetOf()
    private val hours: MutableSet<Hours> = mutableSetOf()
    private val daysOfMonth: MutableSet<DaysOfMonth> = mutableSetOf()
    private val monthsOfYear: MutableSet<MonthsOfYear> = mutableSetOf()
    private val daysOfWeek: MutableSet<DaysOfWeek> = mutableSetOf()

    private var exec: suspend (LocalDateTime) -> Unit = { throw IllegalStateException("no task block has been setup") }

    @Dsl
    fun minute(block: () -> Minutes) {
        minutes.add(block())
    }

    @Dsl
    fun minutes(block: () -> List<Minutes>) {
        minutes.addAll(block())
    }

    @Dsl
    fun hour(block: () -> Hours) {
        hours.add(block())
    }

    @Dsl
    fun hours(block: () -> List<Hours>) {
        hours.addAll(block())
    }

    @Dsl
    fun dayOfMonth(block: () -> DaysOfMonth) {
        daysOfMonth.add(block())
    }

    @Dsl
    fun daysOfMonth(block: () -> List<DaysOfMonth>) {
        daysOfMonth.addAll(block())
    }

    @Dsl
    fun dayOfWeek(block: () -> DaysOfWeek) {
        daysOfWeek.add(block())
    }

    @Dsl
    fun daysOfWeek(block: () -> List<DaysOfWeek>) {
        daysOfWeek.addAll(block())
    }

    @Dsl
    fun monthOfYear(block: () -> MonthsOfYear) {
        monthsOfYear.add(block())
    }

    @Dsl
    fun monthsOfYear(block: () -> List<MonthsOfYear>) {
        monthsOfYear.addAll(block())
    }

    @Dsl
    fun task(block: suspend (LocalDateTime) -> Unit) {
        exec = block
    }

    internal var started = false

    @Dsl
    fun start(): Sukejura {
        launch(
            context = dispatcher
            //newSingleThreadContext("sukejura-background-timer")
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
        val hoursActive = hours.isActive(it.hour)
        val monthsOfYearActive = monthsOfYear.isActive(it.monthValue)
        val daysOfMonthActive = daysOfMonth.isActive(it)
        val daysOfWeekActive = daysOfWeek.isActive(it.dayOfWeek)
//        println(
//            "nowMinute=$nowMinute ## minutesActive=$minutesActive hoursActive=$hoursActive monthsOfYearActive=$monthsOfYearActive " +
//                    "daysOfMonthActive=$daysOfMonthActive daysOfWeekActive=$daysOfWeekActive"
//        )
        listOf(minutesActive, hoursActive, monthsOfYearActive, daysOfMonthActive, daysOfWeekActive)
            .all {
                it
            }
    }

    fun stop() {
        job.cancel()
    }
}

fun Sukejura.invocations(): Sequence<LocalDateTime> = sequence {
    var current = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
    while (true) {
        if (active(current)) {
            yield(current)
        }
        current = current.plusMinutes(1)
    }
}

@DslMarker
annotation class Dsl

fun main(args: Array<String>) {
    System.setProperty(DEBUG_PROPERTY_NAME, "on")
    val sukejura = sukejura {
        minute {
            Minutes.M(11)
        }
        dayOfMonth { DaysOfMonth.Last }
        monthOfYear { MonthsOfYear.Feb }
        hour { Hours.H(15) }

        task { println("running something") }
    }
    val invocations = sukejura.invocations()
    invocations.take(20).forEach {
        println("active: $it")
    }
//    println("now going to sleep 3 minutes")
//    Thread.sleep(TimeUnit.MINUTES.toMillis(2))
//    println("stopping sukejura")
//    sukejura.stop()
//
//    Thread.sleep(TimeUnit.MINUTES.toMillis(2))
//    println("hopefully sukejura stopped")
}
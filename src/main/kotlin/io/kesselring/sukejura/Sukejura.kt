package io.kesselring.sukejura

import com.sun.org.apache.xpath.internal.operations.Bool
import io.kesselring.sukejura.pattern.*
import kotlinx.coroutines.*
import java.time.Clock
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
    internal var clock = Clock.systemDefaultZone()
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

    @Dsl
    fun skipInitialExecution() {
        skipInitialExecution = true
    }

    private var skipInitialExecution = false
    private var isInitialExecution = true
    internal var started = false

    @Dsl
    fun start(): Sukejura {
        launch(
            context = dispatcher
            //newSingleThreadContext("sukejura-background-timer")
        ) {
            var lastCheck: LocalDateTime? = null
            while (true) {
                val nowMinute = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES)
                if (lastCheck == nowMinute) {
                    // already handled $nowMinute
                } else {
                    lastCheck = nowMinute
                    if (active(nowMinute) && skip().not()) {
                        try {
                            exec(nowMinute)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (isInitialExecution) {
                            isInitialExecution = false
                        }
                    }
                }
                delay(50)
            }
        }
        started = true
        return this
    }

    private fun skip(): Boolean = (isInitialExecution && skipInitialExecution).also { isInitialExecution = false }

    private fun active(nowMinute: LocalDateTime): Boolean = nowMinute.let {
        val minutesActive = minutes.isActive(it.minute)
        val hoursActive = hours.isActive(it.hour)
        val monthsOfYearActive = monthsOfYear.isActive(it.monthValue)
        val daysOfMonthActive = daysOfMonth.isActive(it)
        val daysOfWeekActive = daysOfWeek.isActive(it.dayOfWeek)
        listOf(minutesActive, hoursActive, monthsOfYearActive, daysOfMonthActive, daysOfWeekActive)
            .all {
                it
            }
    }

    fun stop() {
        job.cancel()
    }

    fun invocations(): Sequence<LocalDateTime> = sequence {
        var current = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES)
        while (true) {
            if (active(current)) {
                yield(current)
            }
            current = current.plusMinutes(1)
        }
    }
}


@DslMarker
annotation class Dsl
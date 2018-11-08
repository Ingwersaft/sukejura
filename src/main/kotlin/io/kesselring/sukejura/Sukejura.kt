package io.kesselring.sukejura

import io.kesselring.sukejura.pattern.*
import kotlinx.coroutines.*
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * create a new Sukejura instance
 */
@Dsl
fun sukejura(block: Sukejura.() -> Unit): Sukejura {
    val sukejura = Sukejura()
    sukejura.block()
    return sukejura
}

/**
 * Every Sukejura instance has it's own coroutine scope with a single thread executor as dispatcher.
 * Use the dsl function to create an instance
 */
@Dsl
class Sukejura internal constructor() : CoroutineScope {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + dispatcher

    /**
     * Instance schedules. you can change schedules during runtime, e.g. change the task logic
     * Use the schedule dsl function to create an instance
     */
    val schedules: MutableList<Schedule> = mutableListOf()
    internal var clock = Clock.systemDefaultZone()
    private var started = false

    /**
     * create a new schedule
     * @return the created schedule
     */
    fun schedule(init: Schedule.() -> Unit): Schedule {
        val schedule = Schedule(clock).also {
            it.name = scheduleName().first()
            it.init()
        }
        schedules.add(schedule)
        return schedule
    }

    /**
     * Start the Sukejura timer logic
     */
    fun start(): Sukejura {
        launch {
            var lastCheck: LocalDateTime? = null
            while (true) {
                val nowMinute = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES)
                if (lastCheck == nowMinute) {
                    // already handled $nowMinute
                } else {
                    lastCheck = nowMinute
                    coroutineScope {
                        schedules.forEach {
                            launch {
                                if (it.active(nowMinute) && it.skip().not()) {
                                    try {
                                        it.exec(nowMinute)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    if (it.isInitialExecution) {
                                        it.isInitialExecution = false
                                    }
                                }
                            }
                        }
                    }

                }
                delay(50)
            }
        }
        started = true
        return this
    }


    /**
     * Stop Sukejura
     */
    fun stop() {
        job.cancel()
    }

    private fun scheduleName(): Sequence<String> = sequence {
        val atomicInteger = AtomicInteger()
        while (true) {
            yield("schedule-${atomicInteger.incrementAndGet()}")
        }
    }
}

/**
 * Schedule contains the time config and the task to be executed.
 * Use the schedule dsl function to create an instance
 */
@Dsl
class Schedule internal constructor(private val clock: Clock) {
    internal var exec: suspend (LocalDateTime) -> Unit =
        { throw IllegalStateException("no task block has been provided") }
    private val minutes: MutableSet<Minutes> = mutableSetOf()
    private val hours: MutableSet<Hours> = mutableSetOf()
    private val daysOfMonth: MutableSet<DaysOfMonth> = mutableSetOf()
    private val monthsOfYear: MutableSet<MonthsOfYear> = mutableSetOf()
    private val daysOfWeek: MutableSet<DaysOfWeek> = mutableSetOf()

    // used for logging
    lateinit var name: String

    fun minute(block: () -> Minutes) {
        minutes.add(block())
    }

    fun minutes(block: () -> List<Minutes>) {
        minutes.addAll(block())
    }

    fun hour(block: () -> Hours) {
        hours.add(block())
    }

    fun hours(block: () -> List<Hours>) {
        hours.addAll(block())
    }

    fun dayOfMonth(block: () -> DaysOfMonth) {
        daysOfMonth.add(block())
    }

    fun daysOfMonth(block: () -> List<DaysOfMonth>) {
        daysOfMonth.addAll(block())
    }

    fun dayOfWeek(block: () -> DaysOfWeek) {
        daysOfWeek.add(block())
    }

    fun daysOfWeek(block: () -> List<DaysOfWeek>) {
        daysOfWeek.addAll(block())
    }

    fun monthOfYear(block: () -> MonthsOfYear) {
        monthsOfYear.add(block())
    }

    fun monthsOfYear(block: () -> List<MonthsOfYear>) {
        monthsOfYear.addAll(block())
    }

    fun task(block: suspend (LocalDateTime) -> Unit) {
        exec = block
    }

    fun skipInitialExecution() {
        skipInitialExecution = true
    }

    private var skipInitialExecution = false
    internal var isInitialExecution = true

    internal fun skip(): Boolean = (isInitialExecution && skipInitialExecution).also { isInitialExecution = false }

    internal fun active(nowMinute: LocalDateTime): Boolean = nowMinute.let {
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

    /**
     * The next invocations as lazy sequence. Very helpful for checking your scheduling
     */
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
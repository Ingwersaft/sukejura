[ ![Download](https://api.bintray.com/packages/ingwersaft/Sukejura/Sukejura/images/download.svg) ](https://bintray.com/ingwersaft/Sukejura/Sukejura/_latestVersion)

# Sukejura
**cron-like library for kotlin** - coroutine based

If you need something executed in set interval, Sukejura is for you!

Sukejura has minute-precision and supports:
 * Minutes
 * Hours
 * DaysOfWeek
 * DaysOfMonth
 * MonthsOfYear

Sukejura also supports multiple schedules inside the same instance.

## basic example

```kotlin
    val sukejura = sukejura {
        schedule {
            // every working day of the week
            daysOfWeek {
                listOf(
                    DaysOfWeek.Mon,
                    DaysOfWeek.Tue,
                    DaysOfWeek.Wed,
                    DaysOfWeek.Thu,
                    DaysOfWeek.Fri
                )
            }
            // at 9am and 5pm
            hours {
                listOf(
                    Hours.H(9),
                    Hours.H(17)
                )
            }
            // every time the minute is 0
            minute { Minutes.M(0) }
            task {
                println("hello there!")
            }
        }
        start()
    }
    sukejura.schedules.first().invocations().take(20).forEach {
        println("triggering at: $it")
    }

// triggering at: 2018-11-05T09:00
// triggering at: 2018-11-05T17:00
// triggering at: 2018-11-06T09:00
// triggering at: 2018-11-06T17:00
// triggering at: 2018-11-07T09:00
// triggering at: 2018-11-07T17:00
// triggering at: 2018-11-08T09:00
// triggering at: 2018-11-08T17:00
// triggering at: 2018-11-09T09:00
// triggering at: 2018-11-09T17:00
// triggering at: 2018-11-12T09:00
// triggering at: 2018-11-12T17:00
// triggering at: 2018-11-13T09:00
// triggering at: 2018-11-13T17:00
// triggering at: 2018-11-14T09:00
// triggering at: 2018-11-14T17:00
// triggering at: 2018-11-15T09:00
// triggering at: 2018-11-15T17:00
// triggering at: 2018-11-16T09:00
// triggering at: 2018-11-16T17:00
```

For more examples see the examples [subproject](tree/master/examples)

## How to get Sukejura
Sukejura is published at [jcenter](https://bintray.com/ingwersaft/Sukejura/Sukejura).
### Gradle
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
...
dependencies {
    compile 'io.kesselring.sukejura:Sukejura:<version>'
}
```
### Maven
```xml
<repositories>
    <repository>
        <id>central</id>
        <name>bintray</name>
        <url>https://jcenter.bintray.com</url>
    </repository>
</repositories>
...
<dependency>
    <groupId>io.kesselring.sukejura</groupId>
    <artifactId>Sukejura</artifactId>
    <version>#version#</version>
</dependency>
```

## Behaviour
### default values
Default value for every configuration type is `every time`, so creating Sukejura without any
config will result in a task executed every minute.
### Parallelism
Internally, Sukejura uses a single Thread for as dispatcher. 
If you use multiple schedules, the tasks will be executed effectively sequential.
However, if you do non-blocking suspension in your task, you shouldn't have any problems.
### Time tracking
Sukejura only keeps track of the last truncated minute the task has been executed at.
This means:
 * Initial execution won't wait for a full minute (use `skipInitialExecution()` if you want it to wait)
 * If a task execution needs more time than one interval, 
 executions might be skipped (task won't run in parallel)
### multiple schedules
Sukejura supports more than one schedule. 
## about the name

Sukejura or better: Sukejūra / スケジューラ means scheduler in english

## Patterns

Every even Minute:
```kotlin
minutes { (0..59 step 2).map { Minutes.M(it) } }
```

Last Day of February 00:00; only leap years:
```kotlin
monthOfYear { MonthsOfYear.Feb }
dayOfMonth {
    DaysOfMonth.D(29)
}
minute { Minutes.M(0) }
hour { Hours.H(0) }
```
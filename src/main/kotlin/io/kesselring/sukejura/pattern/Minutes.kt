package io.kesselring.sukejura.pattern

import io.kesselring.sukejura.Dsl

@Dsl
sealed class Minutes {
    /**
     * @param value only 0 to 59 is valid
     */
    data class M(val value: Int) : Minutes() {
        init {
            if (IntRange(0, 59).contains(value).not()) {
                throw IllegalArgumentException("given $value not between 0 and 59")
            }
        }
    }

    object Every : Minutes()
}

fun Set<Minutes>.isActive(minute: Int): Boolean {
    val data = if (this.isEmpty()) {
        listOf(Minutes.Every)
    } else {
        this
    }
    val map: List<Int> = data.map {
        when (it) {
            is Minutes.Every -> IntRange(0, 59).map { it }
            is Minutes.M -> listOf(it.value)
        }
    }.flatten()
    return map.contains(minute)
}
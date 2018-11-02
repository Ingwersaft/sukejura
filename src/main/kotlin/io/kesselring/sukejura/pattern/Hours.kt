package io.kesselring.sukejura.pattern

sealed class Hours {
    /**
     * @param value only 0 to 23 is valid
     */
    data class H(val value: Int) : Hours() {
        init {
            if (IntRange(0, 23).contains(value).not()) {
                throw IllegalArgumentException("given $value not between 0 and 23")
            }
        }
    }

    object Every : Hours()
}

fun Set<Hours>.isActive(hour: Int): Boolean {
    val data = if (this.isEmpty()) {
        listOf(Hours.Every)
    } else {
        this
    }
    val map: List<Int> = data.map {
        when (it) {
            is Hours.Every -> IntRange(0, 59).map { it }
            is Hours.H -> listOf(it.value)
        }
    }.flatten()
    return map.contains(hour)
}
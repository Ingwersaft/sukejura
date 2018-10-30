package io.kesselring.kron

import io.kesselring.kron.pattern.Minutes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MinutesTest {
    @Test
    fun testM() {
        assertThrows<IllegalArgumentException> { Minutes.M(-1) }
        assertThrows<IllegalArgumentException> { Minutes.M(60) }
    }
}
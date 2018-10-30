package io.kesselring.kron

import io.kesselring.kron.pattern.Hours
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HoursTest {
    @Test
    fun testH() {
        assertThrows<IllegalArgumentException> { Hours.H(-1) }
        assertThrows<IllegalArgumentException> { Hours.H(24) }
    }
}
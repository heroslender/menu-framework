package com.heroslender.hmf.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StateTests {
    @Test
    fun `immutable state returns correct value`() {
        val state = stateOf(0)
        assertEquals(0, state.value, "Expected 0 from state, but got ${state.value}")

        val stringState = stateOf("Hello World")
        assertEquals("Hello World", stringState.value) {
            "Expected `Hello World` from state, but got ${stringState.value}"
        }

        val charState = stateOf('C')
        assertEquals('C', charState.value) {
            "Expected `C` from state, but got ${charState.value}"
        }
    }

    @Test
    fun `mutable state returns correct value`() {
        val state = mutableStateOf(0)
        assertEquals(0, state.value, "Expected 0 from mutable state, but got ${state.value}")
        state.value++
        assertEquals(1, state.value, "Expected 1 from mutable state, but got ${state.value}")

        val stringState = mutableStateOf("Hello World")
        assertEquals("Hello World", stringState.value) {
            "Expected `Hello World` from mutable state, but got ${stringState.value}"
        }
        stringState.value = "Hello from Foo"
        assertEquals("Hello from Foo", stringState.value) {
            "Expected `Hello from Foo` from mutable state, but got ${stringState.value}"
        }

        val charState = mutableStateOf('C')
        assertEquals('C', charState.value) {
            "Expected `C` from mutable state, but got ${charState.value}"
        }
        charState.value = 'J'
        assertEquals('J', charState.value) {
            "Expected `J` from mutable state, but got ${charState.value}"
        }
    }
}
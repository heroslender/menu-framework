package com.heroslender.hmf.core.font

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class FontTests {

    @Test
    fun `font gets loaded properly`() {
        val font = UBUNTU_MONO_16

        assertNotNull(font['B'], "Character sprite for letter `B` is null, but expected not null.")
        assertNotNull(font['W'], "Character sprite for letter `W` is null, but expected not null.")
        assertNotNull(font['1'], "Character sprite for letter `1` is null, but expected not null.")
        assertNotNull(font['0'], "Character sprite for letter `0` is null, but expected not null.")
    }

    @Test
    fun `font returns expected text width`() {
        val font = UBUNTU_MONO_16

        val textWidth = font.getWidth("Hello World")
        assertEquals(98, textWidth, "Text with expected to be 98 but got $textWidth.")
        val textWidth2 = font.getWidth("Som3 t3x7 w1th num63r5.")
        assertEquals(206, textWidth2, "Text with expected to be 206 but got $textWidth.")
    }
}
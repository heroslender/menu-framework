package com.heroslender.hmf.core.font

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class FontParserTests {
    @Test
    fun `ensure font loads properly`() {
        val ubuntuFont = FontParser.getFontFromResources(
            asset = "fonts/UbuntuMono-Regular.ttf",
            fontName = "Ubuntu Mono"
        )

        val spriteA = ubuntuFont.getChar('A')
        assertNotNull(spriteA, "Character sprite for letter `A` is null, but expected not null.")

        assertNotNull(ubuntuFont['B'], "Character sprite for letter `B` is null, but expected not null.")
        assertNotNull(ubuntuFont['W'], "Character sprite for letter `W` is null, but expected not null.")
        assertNotNull(ubuntuFont['1'], "Character sprite for letter `1` is null, but expected not null.")
        assertNotNull(ubuntuFont['0'], "Character sprite for letter `0` is null, but expected not null.")
        assertNotNull(ubuntuFont['+'], "Character sprite for letter `+` is null, but expected not null.")
        assertNotNull(ubuntuFont['/'], "Character sprite for letter `/` is null, but expected not null.")
    }
}
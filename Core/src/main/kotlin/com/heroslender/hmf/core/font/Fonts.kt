@file:Suppress("unused", "SpellCheckingInspection")

package com.heroslender.hmf.core.font

fun loadFonts() {}

val MINECRAFTIA_8: Font = FontParser.getFontFromResources(
    asset = "fonts/Minecraftia-Regular.ttf",
    fontName = "Minecraftia",
    size = 8
)

val MINECRAFTIA_12: Font = FontParser.getFontFromResources(
    asset = "fonts/Minecraftia-Regular.ttf",
    fontName = "Minecraftia",
    size = 12
)

val MINECRAFTIA_24: Font = FontParser.getFontFromResources(
    asset = "fonts/Minecraftia-Regular.ttf",
    fontName = "Minecraftia",
    size = 24
)

val STAATLICHES_16: Font = FontParser.getFontFromResources(
    asset = "fonts/Staatliches-Regular.ttf",
    fontName = "Staatliches"
)

val UBUNTU_MONO_16: Font = FontParser.getFontFromResources(
    asset = "fonts/UbuntuMono-Regular.ttf",
    fontName = "Ubuntu Mono"
)

val UBUNTU_MONO_24: Font = FontParser.getFontFromResources(
    asset = "fonts/UbuntuMono-Regular.ttf",
    fontName = "Ubuntu Mono",
    size = 24
)

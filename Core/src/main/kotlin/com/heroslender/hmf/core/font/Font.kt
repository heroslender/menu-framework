package com.heroslender.hmf.core.font

import it.unimi.dsi.fastutil.chars.Char2ObjectMap

class Font(
    private val chars: Char2ObjectMap<CharacterSprite>,
) {
    val height: Int = chars.values.maxOf { it.height }

    operator fun get(ch: Char): CharacterSprite? = getChar(ch)

    fun getChar(ch: Char): CharacterSprite? = chars[ch]

    fun getWidth(text: String): Int {
        if (text.isEmpty()) {
            return 0
        }

        var result = 0
        for (element in text) {
            result += getChar(element)?.width ?: 0
        }
        result += text.length - 1

        return result
    }

    fun isValid(text: String): Boolean {
        for (ch in text) {
            if (ch != '§' && ch != '\n' && chars[ch] == null) {
                return false
            }
        }
        return true
    }

    data class CharacterSprite(
        val width: Int,
        val height: Int,
        val data: BooleanArray,
    ) {
        val shadow: BooleanArray = BooleanArray(data.size + width)
        val border: BooleanArray = BooleanArray(data.size + width * 2 + height * 2 + 4)

        init {
            require(width * height == data.size) { "CharacterSprite data size is invalid" }

            stream { x, y ->
                if (!getValue(x, y + 1)) {
                    shadow[(y + 1) * width + x] = true
                }
            }

            stream { spriteX, spriteY ->
                val x = spriteX + 1
                val y = spriteY + 1
                val width = this.width + 2

                if (!getValue(spriteX + 1, spriteY)) {
                    border[y * width + x + 1] = true
                }
                if (!getValue(spriteX - 1, spriteY)) {
                    border[y * width + x - 1] = true
                }
                if (!getValue(spriteX, spriteY + 1)) {
                    border[(y + 1) * width + x] = true
                }
                if (!getValue(spriteX, spriteY - 1)) {
                    border[(y - 1) * width + x] = true
                }
                if (!getValue(spriteX + 1, spriteY + 1)) {
                    border[(y + 1) * width + x + 1] = true
                }
                if (!getValue(spriteX - 1, spriteY - 1)) {
                    border[(y - 1) * width + x - 1] = true
                }
                if (!getValue(spriteX - 1, spriteY + 1)) {
                    border[(y + 1) * width + x - 1] = true
                }
                if (!getValue(spriteX + 1, spriteY - 1)) {
                    border[(y - 1) * width + x + 1] = true
                }
            }
        }

        fun getValue(x: Int, y: Int): Boolean {
            return if (y >= 0 && x >= 0 && y < height && x < width) data[y * width + x] else false
        }

        operator fun get(x: Int, y: Int): Boolean = getValue(x, y)

        inline fun stream(op: (x: Int, y: Int) -> Unit) {
            data.forEachIndexed { i, v ->
                if (v) {
                    val x: Int = i % width
                    val y: Int = i / width

                    op(x, y)
                }
            }
        }

        inline fun streamShadow(op: (x: Int, y: Int) -> Unit) {
            shadow.forEachIndexed { i, v ->
                if (v) {
                    val x: Int = i % width
                    val y: Int = i / width

                    op(x, y)
                }
            }
        }

        inline fun streamBorder(op: (x: Int, y: Int) -> Unit) {
            val width = this.width + 2

            border.forEachIndexed { i, v ->
                if (v) {
                    val x: Int = i % width - 1
                    val y: Int = i / width - 1

                    op(x, y)
                }
            }
        }
    }
}
package com.heroslender.hmf.core.ui_v2.components

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.font.Font
import com.heroslender.hmf.core.font.FontStyle
import com.heroslender.hmf.core.ui_v2.ComponentNode
import com.heroslender.hmf.core.ui_v2.Composable
import com.heroslender.hmf.core.ui_v2.modifier.DrawerModifier
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable
import com.heroslender.hmf.core.ui_v2.modifier.modifiers.fixedSize

fun Composable.Label(
    text: String,
    style: FontStyle,
    modifier: Modifier = Modifier,
) {
    val width = style.font.getWidth(text)
    val height = style.font.height
    val mod = modifier.fixedSize(width, height).then(TextDrawer(text, style))

    val node = ComponentNode(this, mod, renderContext)
    addChild(node)
}

internal class TextDrawer(
    private val text: String,
    private val style: FontStyle,
) : DrawerModifier {
    private val font: Font
        get() = style.font
    private val color: IColor
        get() = style.color
    private val shadowColor: IColor
        get() = style.shadowColor
    private val borderColor: IColor
        get() = style.borderColor


    private fun getTextSubstring(placeable: Placeable): String {
        return if (font.getWidth(text) > placeable.width) {
            var result = 0
            val dotWidth = font.getWidth("...")
            val availableWidth = placeable.width - dotWidth

            var substringIndex = 0
            for (i in text.indices) {
                result += font.getChar(text[i])?.width ?: 0
                // add 1 for spacing between letters
                result++
                if (result < availableWidth) {
                    substringIndex = i
                }
            }

            text.substring(0, substringIndex) + "..."
        } else {
            text
        }
    }

    override fun Placeable.onDraw(canvas: Canvas) {
        val text = getTextSubstring(this)
        var startX = 0
        var startY = 0
        val xStart = startX

        var i = 0
        while (i < text.length) {
            val ch = text[i]
            if (ch == '\n') {
                startX = xStart
                startY += font.height + 1
            } else {
                val sprite = font.getChar(text[i]) ?: continue

                sprite.stream { x, y ->
                    canvas.setPixel(startX + x, startY + y, color)
                }

                if (!borderColor.isTransparent) {
                    sprite.streamBorder { x, y ->
                        canvas.setPixel(startX + x, startY + y, borderColor)
                    }
                } else if (!shadowColor.isTransparent) {
                    sprite.streamShadow { x, y ->
                        canvas.setPixel(startX + x, startY + y, shadowColor)
                    }
                }

                startX += sprite.width + 1
            }

            ++i
        }
    }
}
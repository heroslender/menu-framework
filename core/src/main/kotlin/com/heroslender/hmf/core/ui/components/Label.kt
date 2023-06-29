@file:Suppress("FunctionName")

package com.heroslender.hmf.core.ui.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.compose.Layout
import com.heroslender.hmf.core.font.Font
import com.heroslender.hmf.core.font.FontStyle
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.layout
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.DrawerModifier
import kotlin.math.min

@Composable
fun Label(
    text: String,
    style: FontStyle,
    modifier: Modifier = Modifier,
) = Layout(
    measurableGroup = newMeasurableGroup { _, constraints ->
        val width = min(style.font.getWidth(text), constraints.maxWidth)
        val height = min(style.font.height, constraints.maxHeight)

        layout(width, height)
    },
    modifier =  modifier.then(TextDrawer(text, style))
)

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

        fun Canvas.paint(x: Int, y: Int, color: IColor) {
            if (x < this@onDraw.width && y < this@onDraw.height) {
                setPixel(x, y, color)
            }
        }

        var i = 0
        while (i < text.length) {
            val ch = text[i]
            ++i
            if (ch == '\n') {
                startX = xStart
                startY += font.height + 1
            } else {
                val sprite = font.getChar(ch) ?: continue

                sprite.stream { x, y ->
                    canvas.paint(startX + x, startY + y, color)
                }

                if (!borderColor.isTransparent) {
                    sprite.streamBorder { x, y ->
                        canvas.paint(startX + x, startY + y, borderColor)
                    }
                } else if (!shadowColor.isTransparent) {
                    sprite.streamShadow { x, y ->
                        canvas.paint(startX + x, startY + y, shadowColor)
                    }
                }

                startX += sprite.width + 1
            }

        }
    }
}
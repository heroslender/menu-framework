@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package com.heroslender.hmf.core.ui.components.text

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.font.Font
import com.heroslender.hmf.core.font.FontStyle
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.DrawFunc
import com.heroslender.hmf.core.ui.DrawableComponent
import com.heroslender.hmf.core.ui.modifier.*

inline fun Composable.Label(
    text: String,
    style: FontStyle,
    modifier: Modifier = Modifier,
): LabelComponent {
    val component = LabelComponent(
        text = text,
        style = style,
        modifier = modifier,
        parent = this
    )
    addChild(component)
    return component
}

class LabelComponent(
    text: String,
    style: FontStyle,
    modifier: Modifier,
    parent: Composable,
) : DrawableComponent(parent, modifier) {
    val text: String
    override val contentHeight: Int
    override val contentWidth: Int

    private val font: Font = style.font
    private val color: IColor = style.color
    private val shadowColor: IColor = style.shadowColor
    private val borderColor: IColor = style.borderColor

    init {
        require(font.isValid(text)) { "text contains invalid characters" }

        val textWidth = font.getWidth(text)
        when (modifier.width) {
            is FixedSize -> {
                val horizontalPadding = modifier.padding.horizontal
                val maxWidth = availableWidth
                if (textWidth + horizontalPadding > maxWidth) {
                    var result = 0
                    val dotWidth = font.getWidth("...")
                    val requiredMax = maxWidth - dotWidth - horizontalPadding

                    var substringIndex = 0
                    for (i in text.indices) {
                        result += (font.getChar(text[i]) as Font.CharacterSprite).width
                        if (result > requiredMax) {
                            substringIndex = i
                        }
                    }

                    this.text = text.substring(0, substringIndex) + "..."
                } else {
                    this.text = text
                }

                this.contentWidth = modifier.width.value
            }
            is FitContent, Fill -> {
                this.text = text
                this.contentWidth = textWidth
            }
        }

        this.contentHeight = font.height
    }

    override fun draw(setPixel: DrawFunc) {
        super.draw(setPixel)

        var startX = modifier.padding.left
        var startY = modifier.padding.top
        val xStart = startX
        val color: IColor = this.color

        var i = 0
        while (i < text.length) {
            val ch = text[i]
            if (ch == '\n') {
                startX = xStart
                startY += font.height + 1
            } else {
                val sprite = font.getChar(text[i]) ?: continue

                sprite.stream { x, y ->
                    setPixel(startX + x, startY + y, color)
                }

                if (!borderColor.isTransparent) {
                    sprite.streamBorder { x, y ->
                        setPixel(startX + x, startY + y, borderColor)
                    }
                } else if (!shadowColor.isTransparent) {
                    sprite.streamShadow { x, y ->
                        setPixel(startX + x, startY + y, shadowColor)
                    }
                }

                startX += sprite.width + 1
            }

            ++i
        }
    }
}
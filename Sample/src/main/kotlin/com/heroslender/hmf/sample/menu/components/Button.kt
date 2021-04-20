@file:Suppress("FunctionName")

package com.heroslender.hmf.sample.menu.components

import com.heroslender.hmf.bukkit.map.Color
import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.font.FontStyle
import com.heroslender.hmf.core.font.MINECRAFTIA_8
import com.heroslender.hmf.core.ui.Alignment
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.components.Box
import com.heroslender.hmf.core.ui.components.Label
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.*
import com.heroslender.hmf.core.ui.modifier.type.DrawerModifier

val DefaultFont: FontStyle = FontStyle(font = MINECRAFTIA_8, color = Color.BLACK_1, shadowColor = Color.GRAY_3)

fun Composable.Text(
    text: String,
    style: FontStyle = DefaultFont,
    modifier: Modifier = Modifier,
) {
    Label(text, style, modifier)
}

fun Composable.TextButton(
    text: String,
    fontStyle: FontStyle = DefaultFont,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    padding: PaddingValues = PaddingValues(7, 10),
    colors: ButtonBackgroundColors = ButtonBackgroundColors.Default,
    onClick: ClickListener? = null,
) {
    Button(
        modifier = modifier,
        alignment = alignment,
        padding = padding,
        colors = colors,
        onClick = onClick,
    ) {
        Text(
            text = text,
            style = fontStyle,
        )
    }
}

fun Composable.Button(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    padding: PaddingValues = PaddingValues(7, 10),
    colors: ButtonBackgroundColors = ButtonBackgroundColors.Default,
    onClick: ClickListener? = null,
    content: Composable.() -> Unit,
) {
    val bgDrawer = if (colors === ButtonBackgroundColors.Default) {
        ButtonBackgroundDrawer
    } else {
        ButtonBackgroundDrawer(colors)
    }
    val mod: Modifier = (onClick?.let { modifier.clickable(it) } ?: modifier)
        .border(Color.BLACK_1).then(bgDrawer)
        .padding(padding.top, padding.right, padding.bottom, padding.left)

    Box(
        modifier = mod,
        alignment = alignment,
    ) {
        content()
    }
}

data class ButtonBackgroundColors(
    val main: IColor = Color.WHITE_1,
    val light: IColor = Color.WHITE_6,
    val dark: IColor = Color.GRAY_5,
) {
    companion object {
        val Default: ButtonBackgroundColors = ButtonBackgroundColors()
    }
}

class ButtonBackgroundDrawer(
    val colors: ButtonBackgroundColors,
) : DrawerModifier {
    companion object : DrawerModifier by ButtonBackgroundDrawer(ButtonBackgroundColors.Default)

    override fun Placeable.onDraw(canvas: Canvas) {
        val width = this.width
        val height = this.height
        if (width < 10 || height < 10) {
            return
        }

        val centerColor = colors.main
        val topColor = colors.light
        val bottomColor = colors.dark

        if (!centerColor.isTransparent) {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    canvas.setPixel(x, y, centerColor)
                }
            }
        }

        if (!topColor.isTransparent) {
            for (x in 0 until width) {
                canvas.setPixel(x, 0, topColor)
            }

            for (y in 0 until height) {
                canvas.setPixel(0, y, topColor)
            }
        }

        if (!bottomColor.isTransparent) {
            for (x in 0 until width) {
                canvas.setPixel(x, height - 1, bottomColor)
                canvas.setPixel(x, height - 2, bottomColor)
            }

            for (y in 0 until height) {
                canvas.setPixel(width - 1, y, bottomColor)
            }
        }
    }
}
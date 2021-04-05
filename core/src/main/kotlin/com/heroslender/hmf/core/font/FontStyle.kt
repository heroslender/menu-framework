@file:Suppress("unused")

package com.heroslender.hmf.core.font

import com.heroslender.hmf.core.IColor

open class FontStyle(
    val font: Font,
    val color: IColor,
    val shadowColor: IColor = IColor.TRANSPARENT,
    val borderColor: IColor = IColor.TRANSPARENT,
) {

    fun copy(
        font: Font = this.font,
        color: IColor = this.color,
        shadowColor: IColor = this.shadowColor,
        borderColor: IColor = this.borderColor,
    ): FontStyle = FontStyle(
        font = font,
        color = color,
        shadowColor = shadowColor,
        borderColor = borderColor
    )
}

fun FontStyle.font(font: Font): FontStyle = copy(font = font)

fun FontStyle.color(color: IColor): FontStyle = copy(color = color)

fun FontStyle.shadowColor(color: IColor): FontStyle = copy(shadowColor = color)

fun FontStyle.borderColor(color: IColor): FontStyle = copy(borderColor = color)

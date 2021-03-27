package com.heroslender.hmf.core.ui.modifier

data class MarginValues(
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
)

/**
 * Calculates the horizontal margin for a component. `left + right`
 */
val MarginValues.horizontal: Int
    get() = left + right

/**
 * Calculates the vertical margin for a component. `top + bottom`
 */
val MarginValues.vertical: Int
    get() = top + bottom

fun marginValuesOf(margin: Int): MarginValues =
    marginValuesOf(margin, margin)

fun marginValuesOf(vertical: Int = 0, horizontal: Int = 0): MarginValues =
    marginValuesOf(
        top = vertical,
        right = horizontal,
        bottom = vertical,
        left = horizontal
    )

fun marginValuesOf(top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0): MarginValues =
    MarginValues(
        top = top,
        right = right,
        bottom = bottom,
        left = left
    )

fun marginValuesOf(other: MarginValues): MarginValues =
    MarginValues(
        top = other.top,
        right = other.right,
        bottom = other.bottom,
        left = other.left
    )
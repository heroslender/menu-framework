package com.heroslender.hmf.core.ui_old.modifier

data class PaddingValues(
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
)

/**
 * Calculates the horizontal padding for a component. `left + right`
 */
val PaddingValues.horizontal: Int
    get() = left + right

/**
 * Calculates the vertical padding for a component. `top + bottom`
 */
val PaddingValues.vertical: Int
    get() = top + bottom

fun paddingValuesOf(padding: Int): PaddingValues =
    paddingValuesOf(padding, padding)

fun paddingValuesOf(vertical: Int = 0, horizontal: Int = 0): PaddingValues =
    paddingValuesOf(
        top = vertical,
        right = horizontal,
        bottom = vertical,
        left = horizontal
    )

fun paddingValuesOf(top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0): PaddingValues =
    PaddingValues(
        top = top,
        right = right,
        bottom = bottom,
        left = left
    )

fun paddingValuesOf(other: PaddingValues): PaddingValues =
    PaddingValues(
        top = other.top,
        right = other.right,
        bottom = other.bottom,
        left = other.left
    )
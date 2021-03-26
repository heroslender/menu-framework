package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.modifier.Modifier

/**
 * Calculates the horizontal padding for a component.
 * `left + right`
 */
val Modifier.paddingHorizontal: Int
    get() = paddingLeft + paddingRight

/**
 * Calculates the vertical padding for a component. `top + bottom`
 */
val Modifier.paddingVertical: Int
    get() = paddingTop + paddingBottom

/**
 * Changes the padding of a component on all sides.
 */
fun Modifier.padding(padding: Int): Modifier = padding(padding, padding)

/**
 * Changes the padding of a component vertically and horizontally.
 *
 * The [vertical] padding will be applied to top and bottom.
 * The [horizontal] padding will be applied to left and right.
 */
fun Modifier.padding(
    vertical: Int = paddingTop,
    horizontal: Int = paddingBottom,
): Modifier = padding(
    top = vertical,
    right = horizontal,
    bottom = vertical,
    left = horizontal
)

/**
 * Changes the padding of a component on the given sides.
 */
fun Modifier.padding(
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom,
    left: Int = paddingLeft,
): Modifier = copy(
    paddingTop = top,
    paddingRight = right,
    paddingBottom = bottom,
    paddingLeft = left
)
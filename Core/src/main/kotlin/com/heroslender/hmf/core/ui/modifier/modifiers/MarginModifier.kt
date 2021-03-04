package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.modifier.Modifier

/**
 * Calculates the horizontal margin for a component.
 * `left + right`
 */
val Modifier.marginHorizontal: Int
    get() = marginLeft + marginRight

/**
 * Calculates the vertical margin for a component. `top + bottom`
 */
val Modifier.marginVertical: Int
    get() = marginTop + marginBottom

/**
 * Changes the margin of a component on all sides.
 */
fun Modifier.margin(margin: Int): Modifier = margin(margin, margin)

/**
 * Changes the margin of a component vertically and horizontally.
 *
 * The [vertical] margin will be applied to top and bottom.
 * The [horizontal] margin will be applied to left and right.
 */
fun Modifier.margin(
    vertical: Int = marginTop,
    horizontal: Int = marginLeft,
): Modifier = margin(
    top = vertical,
    right = horizontal,
    bottom = vertical,
    left = horizontal
)

/**
 * Changes the margin of a component on the given sides.
 */
fun Modifier.margin(
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom,
    left: Int = marginLeft,
): Modifier = copy(
    marginTop = top,
    marginRight = right,
    marginBottom = bottom,
    marginLeft = left,
)
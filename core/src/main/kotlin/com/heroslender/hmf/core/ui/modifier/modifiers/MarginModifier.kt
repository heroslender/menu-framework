package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.marginValuesOf

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
    vertical: Int = margin.top,
    horizontal: Int = margin.left,
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
    top: Int = margin.top,
    right: Int = margin.right,
    bottom: Int = margin.bottom,
    left: Int = margin.left,
): Modifier = copy(
    margin = marginValuesOf(
        top = top,
        right = right,
        bottom = bottom,
        left = left
    )
)
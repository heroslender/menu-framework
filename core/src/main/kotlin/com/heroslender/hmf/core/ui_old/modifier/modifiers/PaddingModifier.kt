package com.heroslender.hmf.core.ui_old.modifier.modifiers

import com.heroslender.hmf.core.ui_old.modifier.Modifier
import com.heroslender.hmf.core.ui_old.modifier.paddingValuesOf

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
    vertical: Int = padding.top,
    horizontal: Int = padding.left,
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
    top: Int = padding.top,
    right: Int = padding.right,
    bottom: Int = padding.bottom,
    left: Int = padding.left,
): Modifier = copy(
    padding = paddingValuesOf(top, right, bottom, left)
)
package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.modifier.HorizontalAlignment
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.VerticalAlignment

/**
 * Align the component with the given [alignment] inside the parent.
 *
 * Defines both vertical and horizontal alignments.
 */
fun Modifier.align(
    alignment: Alignment,
): Modifier = align(
    vertical = alignment.vertical,
    horizontal = alignment.horizontal,
)

/**
 * Align the component with the given [vertical] and [horizontal]
 * alignments inside the parent.
 */
fun Modifier.align(
    vertical: VerticalAlignment = verticalAlignment,
    horizontal: HorizontalAlignment = horizontalAlignment,
): Modifier = copy(
    verticalAlignment = vertical,
    horizontalAlignment = horizontal,
)

/**
 * Defines both vertical and horizontal alignments for a component.
 */
enum class Alignment(
    val vertical: VerticalAlignment,
    val horizontal: HorizontalAlignment,
) {
    /**
     * The component will be aligned on the top left corner.
     */
    START(VerticalAlignment.TOP, HorizontalAlignment.START),

    /**
     * The component will be centered.
     */
    CENTER(VerticalAlignment.CENTER, HorizontalAlignment.CENTER),

    /**
     * The component will be aligned on the bottom right corner.
     */
    END(VerticalAlignment.BOTTOM, HorizontalAlignment.END);
}
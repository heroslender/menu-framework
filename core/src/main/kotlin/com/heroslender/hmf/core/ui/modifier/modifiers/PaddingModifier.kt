package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.offset
import com.heroslender.hmf.core.ui.layout

/**
 * Changes the padding of a component on all sides.
 */
inline fun Modifier.padding(padding: Int): Modifier = padding(padding, padding)

/**
 * Changes the padding of a component vertically and horizontally.
 *
 * The [vertical] padding will be applied to top and bottom.
 * The [horizontal] padding will be applied to left and right.
 */
inline fun Modifier.padding(
    vertical: Int = 0,
    horizontal: Int = 0,
): Modifier = padding(
    top = vertical,
    right = horizontal,
    bottom = vertical,
    left = horizontal
)

/**
 * Changes the padding of a component on the given sides.
 */
inline fun Modifier.padding(
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    left: Int = 0,
): Modifier = padding(
    paddingValuesOf(top, right, bottom, left)
)

/**
 * Changes the padding of a component using the given [values].
 */
inline fun Modifier.padding(values: PaddingValues): Modifier = this then PaddingModifier(values)

fun paddingValuesOf(
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    left: Int = 0,
): PaddingValues = PaddingValues(top, right, bottom, left)

/**
 * POJO holding some padding values.
 */
data class PaddingValues(
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
) {
    constructor(vertical: Int = 0, horizontal: Int = 0) : this(vertical, horizontal, vertical, horizontal)

    constructor(padding: Int) : this(padding, padding)

    constructor(values: PaddingValues) : this(values.top, values.right, values.bottom, values.left)
}

class PaddingModifier(
    val values: PaddingValues,
) : LayoutModifier {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val horizontal = values.left + values.right
        val vertical = values.top + values.bottom

        // The inner measurable should be smaller given the current padding.
        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)
        return layout(width, height) {
            placeable.placeAt(values.left, values.top)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaddingModifier

        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.top
        result = 31 * result + values.right
        result = 31 * result + values.bottom
        result = 31 * result + values.left
        return result
    }
}

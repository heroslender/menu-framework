package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.offset

class PaddingModifier(
    val top: Int,
    val right: Int,
    val bottom: Int,
    val left: Int,
) : LayoutModifier {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val horizontal = left + right
        val vertical = top + bottom

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)

        return result(width, height) {
            placeable.placeAt(left, top)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaddingModifier

        if (top != other.top) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false
        if (left != other.left) return false

        return true
    }

    override fun hashCode(): Int {
        var result = top
        result = 31 * result + right
        result = 31 * result + bottom
        result = 31 * result + left
        return result
    }
}

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
fun Modifier.padding(
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    left: Int = 0,
): Modifier = this then PaddingModifier(top, right, bottom, left)
package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.layout

/**
 * Change the component size to be exactly [width] x [height].
 * This will override the parent constraints.
 *
 * If you wish to have the size be constrained to fit the parent
 * constraint, please see [Modifier.size].
 */
fun Modifier.fixedSize(width: Int, height: Int): Modifier =
    this then SizeModifier(
        width,
        width,
        height,
        height,
        ignoreConstraints = true,
    )

/**
 * Change the component size to be [width]x[height].
 * These values may be adapted to fit the parent constraints.
 *
 * If you wish to override the parent constraints, please
 * see [Modifier.fixedSize].
 */
fun Modifier.size(width: Int = Constraints.Infinity, height: Int = Constraints.Infinity): Modifier =
    this then SizeModifier(
        width,
        width,
        height,
        height,
        ignoreConstraints = false,
    )

/**
 * Add a size constraint to the component to be limited at a
 * maximum [width] and [height].
 */
fun Modifier.maxSize(width: Int = Constraints.Infinity, height: Int = Constraints.Infinity): Modifier =
    this then SizeModifier(
        0,
        width,
        0,
        height,
        ignoreConstraints = false
    )

internal class SizeModifier(
    private val minWidth: Int = 0,
    private val maxWidth: Int = Constraints.Infinity,
    private val minHeight: Int = 0,
    private val maxHeight: Int = Constraints.Infinity,
    private val ignoreConstraints: Boolean = false,
) : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val newConstraints = if (ignoreConstraints) {
            Constraints(minWidth, maxWidth, minHeight, maxHeight)
        } else {
            Constraints(
                constraints.constrainWidth(minWidth),
                constraints.constrainWidth(maxWidth),
                constraints.constrainHeight(minHeight),
                constraints.constrainHeight(maxHeight),
            )
        }

        val placeable = measurable.measure(newConstraints)

        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}

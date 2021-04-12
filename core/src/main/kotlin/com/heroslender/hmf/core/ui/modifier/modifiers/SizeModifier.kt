package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.layout

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

fun Modifier.fixedSize(width: Int, height: Int): Modifier =
    this then SizeModifier(
        width,
        width,
        height,
        height,
        ignoreConstraints = false,
    )

fun Modifier.maxSize(width: Int = Constraints.Infinity, height: Int = Constraints.Infinity): Modifier =
    this then SizeModifier(
        0,
        width,
        0,
        height,
        ignoreConstraints = false
    )
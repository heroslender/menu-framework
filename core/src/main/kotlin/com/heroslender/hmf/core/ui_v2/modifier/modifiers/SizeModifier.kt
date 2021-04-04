package com.heroslender.hmf.core.ui_v2.modifier.modifiers

import com.heroslender.hmf.core.ui_v2.modifier.LayoutModifier
import com.heroslender.hmf.core.ui_v2.Measurable
import com.heroslender.hmf.core.ui_v2.MeasureScope
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier

class SizeModifier(
    val width: Int,
    val height: Int,
) : LayoutModifier {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val width = constraints.constrainWidth(width)
        val height = constraints.constrainHeight(height)

        val newConstraints = Constraints(width, width, height, height)
        val placeable = measurable.measure(newConstraints)

        return result(width, height) {
            placeable.placeAt(0, 0)
        }
    }
}

fun Modifier.fixedSize(width: Int, height: Int): Modifier = this then SizeModifier(width, height)

class LimitedSizeModifier(
    val width: Int,
    val height: Int,
) : LayoutModifier {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val width = constraints.constrainWidth(width)
        val height = constraints.constrainHeight(height)

        val newConstraints = Constraints(constraints.minWidth, width, constraints.minHeight, height)
        val placeable = measurable.measure(newConstraints)

        return result(width, height) {
            placeable.placeAt(0, 0)
        }
    }
}

fun Modifier.limitedSize(width: Int, height: Int): Modifier = this then LimitedSizeModifier(width, height)
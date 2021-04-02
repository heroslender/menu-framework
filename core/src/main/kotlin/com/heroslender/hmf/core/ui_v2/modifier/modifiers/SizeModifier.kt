package com.heroslender.hmf.core.ui_v2.modifier.modifiers

import com.heroslender.hmf.core.ui_v2.LayoutModifier
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
package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import kotlin.math.max

fun Composable.Box(
    modifier: Modifier,
    alignment: Alignment = Alignment.TopStart,
    content: Composable.() -> Unit,
) = appendComposable(modifier, content) {
    measurableGroup = boxMeasurableGroup(alignment)
}

fun Composable.Box(
    modifier: Modifier,
) = appendComponent(modifier) {
    measurableGroup = EmptyMeasurableGroup
}

object EmptyMeasurableGroup : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = result(constraints.minWidth, constraints.minHeight) {}
}

private fun boxMeasurableGroup(
    alignment: Alignment,
): MeasurableGroup = object : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = when {
        measurables.isEmpty() -> result(constraints.minWidth, constraints.minHeight) {}
        else -> {
            var width = constraints.minWidth
            var height = constraints.minHeight
            val placeables = arrayOfNulls<Placeable>(measurables.size)
            measurables.forEachIndexed { index, measurable ->
                val placeable = measurable.measure(constraints)

                width = max(width, placeable.width)
                height = max(height, placeable.height)

                placeables[index] = placeable
            }

            result(width, height) {
                for (placeable in placeables) {
                    placeable!!
                    val (x, y) = alignment.align(width - placeable.width, height - placeable.height)
                    placeable.placeAt(x, y)
                }
            }
        }
    }
}

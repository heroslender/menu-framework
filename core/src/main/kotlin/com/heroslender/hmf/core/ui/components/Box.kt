package com.heroslender.hmf.core.ui.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.compose.Layout
import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import kotlin.math.max

@Composable
fun Box(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) = Layout(boxMeasurableGroup(alignment), modifier, content)

@Composable
fun Box(
    modifier: Modifier = Modifier,
) = Layout(EmptyMeasurableGroup, modifier)

object EmptyMeasurableGroup : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = layout(constraints.minWidth, constraints.minHeight)
}

private fun boxMeasurableGroup(
    alignment: Alignment,
): MeasurableGroup = object : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = when {
        measurables.isEmpty() -> layout(constraints.minWidth, constraints.minHeight) {}
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

            layout(width, height) {
                for (placeable in placeables) {
                    placeable!!
                    val (x, y) = alignment.align(width - placeable.width, height - placeable.height)
                    placeable.placeAt(x, y)
                }
            }
        }
    }
}

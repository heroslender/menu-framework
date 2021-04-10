package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.modifier.type.MeasurableDataModifier
import kotlin.math.max
import kotlin.math.sign

fun Composable.Row(
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Horizontal.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Vertical.Top,
    content: Composable.() -> Unit,
) = appendComposable(modifier, content) {
    measurableGroup = orientedCopmonentMeasurableGroup(Orientation.HORIZONTAL, horizontalArrangement, verticalAlignment)
}

fun Composable.Column(
    modifier: Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Vertical.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Horizontal.Start,
    content: Composable.() -> Unit,
) = appendComposable(modifier, content) {
    measurableGroup = orientedCopmonentMeasurableGroup(Orientation.VERTICAL, verticalArrangement, horizontalAlignment)
}

enum class Direction {
    HORIZONTAL,
    VERTICAL,
    BOTH,
}

fun Modifier.fillWidth(fraction: Float = 1F): Modifier = this then FillModifier(Direction.HORIZONTAL, fraction)
fun Modifier.fillHeight(fraction: Float = 1F): Modifier = this then FillModifier(Direction.VERTICAL, fraction)
fun Modifier.fillSize(fraction: Float = 1F): Modifier = this then FillModifier(Direction.BOTH, fraction)

internal class FillModifier(
    val direction: Direction,
    val fraction: Float,
) : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val minWidth: Int
        val maxWidth: Int
        if (direction != Direction.VERTICAL) {
            val width = constraints.constrainWidth(
                (constraints.maxWidth * fraction).toInt()
            )
            minWidth = width
            maxWidth = width
        } else {
            minWidth = constraints.minWidth
            maxWidth = constraints.maxWidth
        }

        val minHeight: Int
        val maxHeight: Int
        if (direction != Direction.HORIZONTAL) {
            val height = constraints.constrainHeight(
                (constraints.maxHeight * fraction).toInt()
            )
            minHeight = height
            maxHeight = height
        } else {
            minHeight = constraints.minHeight
            maxHeight = constraints.maxHeight
        }

        val placeable = measurable.measure(Constraints(minWidth, maxWidth, minHeight, maxHeight))

        return result(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}

fun Modifier.weight(weight: Int) = this then ContainerWeightModifier(weight)

val Measurable.weight: Int
    get() = (data as? ContainerData)?.weight ?: 0

internal class ContainerWeightModifier(val weight: Int) : MeasurableDataModifier {
    override fun modifyData(data: Any?): ContainerData {
        return (data as? ContainerData ?: ContainerData()).also {
            it.weight = weight
        }
    }
}

data class ContainerData(
    var weight: Int = 0,
)

val ContainerData?.weight: Int
    get() = this?.weight ?: 0

val Measurable.containerData: ContainerData?
    get() = (data as? ContainerData)

data class AxisConstraints(
    var mainAxisMin: Int,
    var mainAxisMax: Int,
    var crossAxisMin: Int,
    var crossAxisMax: Int,
)

fun Constraints.toAxisConstraints(orientation: Orientation): AxisConstraints =
    if (orientation == Orientation.HORIZONTAL)
        AxisConstraints(minWidth, maxWidth, minHeight, maxHeight)
    else
        AxisConstraints(minHeight, maxHeight, minWidth, maxWidth)

fun AxisConstraints.toConstraints(orientation: Orientation): Constraints =
    if (orientation == Orientation.HORIZONTAL)
        Constraints(mainAxisMin, mainAxisMax, crossAxisMin, crossAxisMax)
    else
        Constraints(crossAxisMin, crossAxisMax, mainAxisMin, mainAxisMax)

fun Placeable.mainAxisSize(orientation: Orientation): Int =
    if (orientation == Orientation.HORIZONTAL) width else height

fun Placeable.crossAxisSize(orientation: Orientation): Int =
    if (orientation == Orientation.HORIZONTAL) height else width

val Placeable.hasBoundingBox: Boolean
    get() = width > 0 && height > 0

private fun orientedCopmonentMeasurableGroup(
    orientation: Orientation,
    mainAxisArrangment: Arrangement,
    crossAxisAlignment: Alignment.Directional,
): MeasurableGroup = object : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = when {
        measurables.isEmpty() -> result(constraints.minWidth, constraints.minHeight) {}
        else -> {
            val axisConstraints = constraints.toAxisConstraints(orientation)
            val placeables = arrayOfNulls<Placeable>(measurables.size)
            val datas = Array(measurables.size) { i -> measurables[i].containerData }

            var totalWeight = 0
            var weightCount = 0

            var usedSize = 0
            var crossAxisUsedSize = 0
            measurables.forEachIndexed { index, measurable ->
                val data = datas[index]
                val weight = data?.weight ?: 0

                if (weight > 0) {
                    totalWeight += weight
                    ++weightCount
                } else {
                    val placeable = measurable.measure(
                        axisConstraints.copy(
                            mainAxisMin = 0,
                            mainAxisMax = if (axisConstraints.mainAxisMax == Constraints.Infinity) {
                                axisConstraints.mainAxisMax
                            } else {
                                axisConstraints.mainAxisMax - usedSize
                            },
                            crossAxisMin = 0
                        ).toConstraints(orientation)
                    )
                    usedSize += placeable.mainAxisSize(orientation)
                    crossAxisUsedSize = max(crossAxisUsedSize, placeable.crossAxisSize(orientation))
                    placeables[index] = placeable
                }
            }

            if (weightCount > 0) {
                val freeSize = axisConstraints.mainAxisMax - usedSize
                val weightUnitSize = freeSize / totalWeight
                var remainder = freeSize - datas.sumBy { it.weight * weightUnitSize }

                for (i in measurables.indices) {
                    if (placeables[i] != null) {
                        continue
                    }

                    val measurable = measurables[i]
                    val data = datas[i]
                    val weight = data?.weight ?: 0
                    require(weight > 0) { "Non weighted components should have been measured already" }

                    val remainderSign = remainder.sign
                    remainder -= remainderSign
                    val childSize = max(0, weightUnitSize * weight + remainder)
                    val placeable = measurable.measure(
                        axisConstraints.copy(
                            mainAxisMin = childSize,
                            mainAxisMax = childSize,
                            crossAxisMin = 0,
                        ).toConstraints(orientation)
                    )
                    usedSize += placeable.mainAxisSize(orientation)
                    crossAxisUsedSize = max(crossAxisUsedSize, placeable.crossAxisSize(orientation))
                    placeables[i] = placeable
                }
            }

            val mainAxisSize = max(usedSize, axisConstraints.mainAxisMin)
            val crossAxisSize = max(crossAxisUsedSize, axisConstraints.crossAxisMin)

            val width = if (orientation == Orientation.HORIZONTAL) mainAxisSize else crossAxisSize
            val height = if (orientation == Orientation.HORIZONTAL) crossAxisSize else mainAxisSize
            result(width, height) {
                val sizes = IntArray(placeables.size) { i -> placeables[i]!!.mainAxisSize(orientation) }
                val outPositions = IntArray(placeables.size)
                mainAxisArrangment.arrange(mainAxisSize, sizes, outPositions)

                placeables.forEachIndexed { index, placeable ->
                    placeable!!
                    if (!placeable.hasBoundingBox) {
                        placeable.isVisible = false
                        return@forEachIndexed
                    }

                    val crossAxisPos =
                        crossAxisAlignment.align(crossAxisUsedSize - placeable.crossAxisSize(orientation))

                    val x: Int
                    val y: Int
                    if (orientation == Orientation.HORIZONTAL) {
                        x = outPositions[index]
                        y = crossAxisPos
                    } else {
                        x = crossAxisPos
                        y = outPositions[index]
                    }
                    placeable.placeAt(x, y)

                    if (x > constraints.maxWidth || y > constraints.maxHeight) {
                        placeable.isVisible = false
                    }
                }
            }
        }
    }
}
package com.heroslender.hmf.core.ui_v2.components

import com.heroslender.hmf.core.ui.Orientation
import com.heroslender.hmf.core.ui_v2.*
import com.heroslender.hmf.core.ui_v2.modifier.*
import kotlin.math.max
import kotlin.math.sign

fun Composable.Row(
    modifier: Modifier,
    content: Composable.() -> Unit,
) {
    val node = ComposableNode(this, modifier, renderContext, content).apply {
        measurableGroup = orientedCopmonentMeasurableGroup(Orientation.HORIZONTAL)
    }

    addChild(node)
}

fun Composable.Column(
    modifier: Modifier,
    content: Composable.() -> Unit,
) {
    val node = ComposableNode(this, modifier, renderContext, content).apply {
        measurableGroup = orientedCopmonentMeasurableGroup(Orientation.VERTICAL)
    }

    addChild(node)
}

fun Composable.Box(
    modifier: Modifier,
    content: Composable.() -> Unit,
) {
    val node = ComposableNode(this, modifier, renderContext, content)
    addChild(node)
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

        val placeable = measurable.measure(Constraints(minWidth,
            maxWidth,
            minHeight,
            maxHeight).also { println("$constraints > $it") })

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
): MeasurableGroup = object : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = when {
        measurables.isEmpty() -> result(constraints.minWidth, constraints.minHeight) {}
        measurables.size == 1 -> {
            val placeable = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
            result(placeable.width, placeable.height) {
                placeable.placeAt(0, 0)
            }
        }
        else -> {
            val axisConstraints = constraints.toAxisConstraints(orientation)
            val placeables = arrayOfNulls<Placeable>(measurables.size)
            val datas = Array(measurables.size) { i -> measurables[i].containerData }

            var totalWeight = 0
            var weightCount = 0

            var usedSize = 0
            var crossAxisSize = 0
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
                    crossAxisSize = max(crossAxisSize, placeable.crossAxisSize(orientation))
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
                    crossAxisSize = max(crossAxisSize, placeable.crossAxisSize(orientation))
                    placeables[i] = placeable
                }
            }

            val width = if (orientation == Orientation.HORIZONTAL) usedSize else crossAxisSize
            val height = if (orientation == Orientation.HORIZONTAL) crossAxisSize else usedSize
            var x = 0
            var y = 0
            result(constraints.constrainWidth(width), constraints.constrainHeight(height)) {
                for (placeable in placeables) {
                    if (placeable == null || !placeable.hasBoundingBox) continue

                    placeable.placeAt(x, y)
                    if (x > constraints.maxWidth || y > constraints.maxHeight) {
                        placeable.isVisible = false
                    }

                    x += placeable.width * orientation.x
                    y += placeable.height * orientation.y
                }
            }
        }
    }
}
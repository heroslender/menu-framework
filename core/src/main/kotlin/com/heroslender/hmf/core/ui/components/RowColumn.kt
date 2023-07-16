package com.heroslender.hmf.core.ui.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.compose.Layout
import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.modifier.type.MeasurableDataModifier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Vertical.Top,
    content: @Composable () -> Unit,
) = Layout(
    measurableGroup = orientedCopmonentMeasurableGroup(
        Orientation.HORIZONTAL,
        horizontalArrangement,
        verticalAlignment
    ),
    modifier = modifier,
    content = content,
    name = "Row"
)

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Horizontal.Start,
    content: @Composable () -> Unit,
) = Layout(
    measurableGroup = orientedCopmonentMeasurableGroup(Orientation.VERTICAL, verticalArrangement, horizontalAlignment),
    modifier = modifier,
    content = content,
    name = "Column"
)

enum class Direction {
    HORIZONTAL,
    VERTICAL,
    BOTH,
}

fun Modifier.fillWidth(fraction: Float = 1F): Modifier = this then FillModifier(Direction.HORIZONTAL, fraction)
fun Modifier.fillHeight(fraction: Float = 1F): Modifier = this then FillModifier(Direction.VERTICAL, fraction)
fun Modifier.fillSize(fraction: Float = 1F): Modifier = this then FillModifier(Direction.BOTH, fraction)

fun Modifier.wrapContentWidth(): Modifier = this then WrapContentModifier(Direction.HORIZONTAL)
fun Modifier.wrapContentHeight(): Modifier = this then WrapContentModifier(Direction.VERTICAL)
fun Modifier.wrapContentSize(): Modifier = this then WrapContentModifier(Direction.BOTH)

internal class WrapContentModifier(
    private val direction: Direction,
) : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val minWidth = if (direction != Direction.VERTICAL) 0 else constraints.minWidth
        val minHeight = if (direction != Direction.HORIZONTAL) 0 else constraints.minHeight

        val newConstraints = Constraints(
            minWidth = minWidth,
            minHeight = minHeight,
            maxWidth = constraints.maxWidth,
            maxHeight = constraints.maxHeight
        )

        val placeable = measurable.measure(newConstraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}

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

        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}

fun Modifier.weight(weight: Int) = this then ContainerWeightModifier(weight)

val Measurable.weight: Int
    get() = (parentData as? ContainerData)?.weight ?: 0

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

val IntrinsicMeasurable.containerData: ContainerData?
    get() = (parentData as? ContainerData)

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
        measurables.isEmpty() -> layout(constraints.minWidth, constraints.minHeight) {}
        else -> {
            val placeables = arrayOfNulls<Placeable>(measurables.size)

            val measurementHelper = RowColumnMeasurementHelper(
                orientation,
                mainAxisArrangment,
                mainAxisArrangment.spacing,
                crossAxisAlignment,
                measurables,
                placeables
            )

            val measureResult = measurementHelper.measureWithoutPlacing(constraints, 0, measurables.size)

            val width: Int
            val height: Int
            if (orientation == Orientation.HORIZONTAL) {
                width = measureResult.mainAxisSize
                height = measureResult.crossAxisSize
            } else {
                width = measureResult.crossAxisSize
                height = measureResult.mainAxisSize
            }

            layout(width, height) {
                measurementHelper.placeHelper(measureResult, 0)
            }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ) = MinIntrinsicWidthMeasureBlock(orientation)(
        measurables,
        height,
        0
    )

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ) = MinIntrinsicHeightMeasureBlock(orientation)(
        measurables,
        width,
        0
    )

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ) = MaxIntrinsicWidthMeasureBlock(orientation)(
        measurables,
        height,
        0
    )

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ) = MaxIntrinsicHeightMeasureBlock(orientation)(
        measurables,
        width,
        0
    )
}

private fun MinIntrinsicWidthMeasureBlock(orientation: Orientation) =
    if (orientation == Orientation.HORIZONTAL) {
        IntrinsicMeasureBlocks.HorizontalMinWidth
    } else {
        IntrinsicMeasureBlocks.VerticalMinWidth
    }

private fun MinIntrinsicHeightMeasureBlock(orientation: Orientation) =
    if (orientation == Orientation.HORIZONTAL) {
        IntrinsicMeasureBlocks.HorizontalMinHeight
    } else {
        IntrinsicMeasureBlocks.VerticalMinHeight
    }

private fun MaxIntrinsicWidthMeasureBlock(orientation: Orientation) =
    if (orientation == Orientation.HORIZONTAL) {
        IntrinsicMeasureBlocks.HorizontalMaxWidth
    } else {
        IntrinsicMeasureBlocks.VerticalMaxWidth
    }

private fun MaxIntrinsicHeightMeasureBlock(orientation: Orientation) =
    if (orientation == Orientation.HORIZONTAL) {
        IntrinsicMeasureBlocks.HorizontalMaxHeight
    } else {
        IntrinsicMeasureBlocks.VerticalMaxHeight
    }

private object IntrinsicMeasureBlocks {
    val HorizontalMinWidth: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableHeight, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { h -> minIntrinsicWidth(h) },
                { w -> maxIntrinsicHeight(w) },
                availableHeight,
                mainAxisSpacing,
                Orientation.HORIZONTAL,
                Orientation.HORIZONTAL
            )
        }
    val VerticalMinWidth: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableHeight, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { h -> minIntrinsicWidth(h) },
                { w -> maxIntrinsicHeight(w) },
                availableHeight,
                mainAxisSpacing,
                Orientation.VERTICAL,
                Orientation.HORIZONTAL
            )
        }
    val HorizontalMinHeight: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableWidth, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { w -> minIntrinsicHeight(w) },
                { h -> maxIntrinsicWidth(h) },
                availableWidth,
                mainAxisSpacing,
                Orientation.HORIZONTAL,
                Orientation.VERTICAL
            )
        }
    val VerticalMinHeight: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableWidth, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { w -> minIntrinsicHeight(w) },
                { h -> maxIntrinsicWidth(h) },
                availableWidth,
                mainAxisSpacing,
                Orientation.VERTICAL,
                Orientation.VERTICAL
            )
        }
    val HorizontalMaxWidth: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableHeight, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { h -> maxIntrinsicWidth(h) },
                { w -> maxIntrinsicHeight(w) },
                availableHeight,
                mainAxisSpacing,
                Orientation.HORIZONTAL,
                Orientation.HORIZONTAL
            )
        }
    val VerticalMaxWidth: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableHeight, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { h -> maxIntrinsicWidth(h) },
                { w -> maxIntrinsicHeight(w) },
                availableHeight,
                mainAxisSpacing,
                Orientation.VERTICAL,
                Orientation.HORIZONTAL
            )
        }
    val HorizontalMaxHeight: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableWidth, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { w -> maxIntrinsicHeight(w) },
                { h -> maxIntrinsicWidth(h) },
                availableWidth,
                mainAxisSpacing,
                Orientation.HORIZONTAL,
                Orientation.VERTICAL
            )
        }
    val VerticalMaxHeight: (List<IntrinsicMeasurable>, Int, Int) -> Int =
        { measurables, availableWidth, mainAxisSpacing ->
            intrinsicSize(
                measurables,
                { w -> maxIntrinsicHeight(w) },
                { h -> maxIntrinsicWidth(h) },
                availableWidth,
                mainAxisSpacing,
                Orientation.VERTICAL,
                Orientation.VERTICAL
            )
        }
}

private fun intrinsicSize(
    children: List<IntrinsicMeasurable>,
    intrinsicMainSize: IntrinsicMeasurable.(Int) -> Int,
    intrinsicCrossSize: IntrinsicMeasurable.(Int) -> Int,
    crossAxisAvailable: Int,
    mainAxisSpacing: Int,
    layoutOrientation: Orientation,
    intrinsicOrientation: Orientation,
) = if (layoutOrientation == intrinsicOrientation) {
    intrinsicMainAxisSize(children, intrinsicMainSize, crossAxisAvailable, mainAxisSpacing)
} else {
    intrinsicCrossAxisSize(children, intrinsicCrossSize, intrinsicMainSize, crossAxisAvailable)
}


private fun intrinsicMainAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSize: IntrinsicMeasurable.(Int) -> Int,
    crossAxisAvailable: Int,
    mainAxisSpacing: Int,
): Int {
    var weightUnitSpace = 0
    var fixedSpace = 0
    var totalWeight = 0f
    children.forEach { child ->
        val weight = child.containerData.weight
        val size = child.mainAxisSize(crossAxisAvailable)
        if (weight == 0) {
            fixedSpace += size
        } else if (weight > 0f) {
            totalWeight += weight
            weightUnitSpace = max(weightUnitSpace, size / weight)
        }
    }
    return (weightUnitSpace * totalWeight).roundToInt() + fixedSpace +
        (children.size - 1) * mainAxisSpacing
}

private fun intrinsicCrossAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSize: IntrinsicMeasurable.(Int) -> Int,
    crossAxisSize: IntrinsicMeasurable.(Int) -> Int,
    mainAxisAvailable: Int,
): Int {
    var fixedSpace = 0
    var crossAxisMax = 0
    var totalWeight = 0f
    children.forEach { child ->
        val weight = child.containerData.weight
        if (weight == 0) {
            // Ask the child how much main axis space it wants to occupy. This cannot be more
            // than the remaining available space.
            val mainAxisSpace = min(
                child.mainAxisSize(Constraints.Infinity),
                mainAxisAvailable - fixedSpace
            )
            fixedSpace += mainAxisSpace
            // Now that the assigned main axis space is known, ask about the cross axis space.
            crossAxisMax = max(crossAxisMax, child.crossAxisSize(mainAxisSpace))
        } else if (weight > 0f) {
            totalWeight += weight
        }
    }

    // For weighted children, calculate how much main axis space weight=1 would represent.
    val weightUnitSpace = if (totalWeight == 0f) {
        0
    } else if (mainAxisAvailable == Constraints.Infinity) {
        Constraints.Infinity
    } else {
        (max(mainAxisAvailable - fixedSpace, 0) / totalWeight).roundToInt()
    }

    children.forEach { child ->
        val weight = child.containerData.weight
        // Now the main axis for weighted children is known, so ask about the cross axis space.
        if (weight > 0) {
            crossAxisMax = max(
                crossAxisMax,
                child.crossAxisSize(
                    if (weightUnitSpace != Constraints.Infinity) {
                        weightUnitSpace * weight
                    } else {
                        Constraints.Infinity
                    }
                )
            )
        }
    }
    return crossAxisMax
}

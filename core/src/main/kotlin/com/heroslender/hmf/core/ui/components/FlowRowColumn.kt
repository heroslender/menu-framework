package com.heroslender.hmf.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.collection.mutableVectorOf
import com.heroslender.hmf.core.compose.Layout
import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import kotlin.math.max

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable () -> Unit,
) = Layout(
    measurableGroup = flowMeasurePolicy(
        Orientation.HORIZONTAL,
        horizontalArrangement,
        horizontalArrangement.spacing,
        Alignment.Vertical.Top,
        verticalArrangement,
        verticalArrangement.spacing,
        maxItemsInEachRow
    ),
    modifier = modifier,
    content = content,
    name = "Row"
)

@Composable
fun FlowColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable () -> Unit,
) = Layout(
    measurableGroup = flowMeasurePolicy(
        Orientation.VERTICAL,
        verticalArrangement,
        verticalArrangement.spacing,
        Alignment.Horizontal.Start,
        horizontalArrangement,
        horizontalArrangement.spacing,
        maxItemsInEachRow
    ),
    modifier = modifier,
    content = content,
    name = "Row"
)


/**
 * Returns a Flow Measure Policy
 */
private fun flowMeasurePolicy(
    orientation: Orientation,
    mainAxisArrangement: Arrangement,
    mainAxisArrangementSpacing: Int,
    crossAxisAlignment: Alignment.Directional,
    crossAxisArrangement: Arrangement,
    crossAxisArrangementSpacing: Int,
    maxItemsInMainAxis: Int,
): MeasurableGroup {
    return object : MeasurableGroup {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints,
        ): MeasureScope.MeasureResult {
            if (measurables.isEmpty()) {
                return layout(0, 0) {}
            }
            val placeables: Array<Placeable?> = arrayOfNulls(measurables.size)
            val measureHelper = RowColumnMeasurementHelper(
                orientation,
                mainAxisArrangement,
                mainAxisArrangementSpacing,
                crossAxisAlignment,
                measurables,
                placeables,
            )
            val orientationIndependentConstraints = constraints.toAxisConstraints(orientation)
            val flowResult = breakDownItems(
                measureHelper,
                orientation,
                orientationIndependentConstraints,
                maxItemsInMainAxis,
            )
            val items = flowResult.items
            val crossAxisSizes = IntArray(items.size) { index ->
                items[index].crossAxisSize
            }
            // space in between children, except for the last child
            val outPosition = IntArray(crossAxisSizes.size)
            var totalCrossAxisSize = flowResult.crossAxisTotalSize
            val totalCrossAxisSpacing = crossAxisArrangementSpacing * (items.size - 1)
            totalCrossAxisSize += totalCrossAxisSpacing
            crossAxisArrangement.arrange(
                totalCrossAxisSize,
                crossAxisSizes,
                outPosition
            )

            var layoutWidth: Int
            var layoutHeight: Int
            if (orientation == Orientation.HORIZONTAL) {
                layoutWidth = flowResult.mainAxisTotalSize
                layoutHeight = totalCrossAxisSize
            } else {
                layoutWidth = totalCrossAxisSize
                layoutHeight = flowResult.mainAxisTotalSize
            }
            layoutWidth = constraints.constrainWidth(layoutWidth)
            layoutHeight = constraints.constrainHeight(layoutHeight)
            return layout(layoutWidth, layoutHeight) {
                flowResult.items.forEachIndexed { currentRowOrColumnIndex, measureResult ->
                    measureHelper.placeHelper(
                        measureResult,
                        outPosition[currentRowOrColumnIndex],
                    )
                }
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int,
        ) = if (orientation == Orientation.HORIZONTAL) {
            minIntrinsicMainAxisSize(
                measurables,
                height,
                mainAxisArrangementSpacing,
                crossAxisArrangementSpacing
            )
        } else {
            intrinsicCrossAxisSize(
                measurables,
                height,
                mainAxisArrangementSpacing,
                crossAxisArrangementSpacing
            )
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int,
        ) = if (orientation == Orientation.HORIZONTAL) {
            intrinsicCrossAxisSize(
                measurables,
                width,
                mainAxisArrangementSpacing,
                crossAxisArrangementSpacing
            )
        } else {
            minIntrinsicMainAxisSize(
                measurables,
                width,
                mainAxisArrangementSpacing,
                crossAxisArrangementSpacing,
            )
        }

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int,
        ) = if (orientation == Orientation.HORIZONTAL) {
            intrinsicCrossAxisSize(
                measurables,
                width,
                mainAxisArrangementSpacing,
                crossAxisArrangementSpacing
            )
        } else {
            maxIntrinsicMainAxisSize(
                measurables,
                width,
                mainAxisArrangementSpacing,
            )
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int,
        ) = if (orientation == Orientation.HORIZONTAL) {
            maxIntrinsicMainAxisSize(
                measurables,
                height,
                mainAxisArrangementSpacing,
            )
        } else {
            intrinsicCrossAxisSize(
                measurables,
                height,
                mainAxisArrangementSpacing,
                crossAxisArrangementSpacing
            )
        }

        fun minIntrinsicMainAxisSize(
            measurables: List<IntrinsicMeasurable>,
            crossAxisAvailable: Int,
            mainAxisSpacing: Int,
            crossAxisSpacing: Int,
        ) = minIntrinsicMainAxisSize(
            measurables,
            mainAxisSize = minMainAxisIntrinsicItemSize,
            crossAxisSize = minCrossAxisIntrinsicItemSize,
            crossAxisAvailable,
            mainAxisSpacing,
            crossAxisSpacing,
            maxItemsInMainAxis
        )

        fun maxIntrinsicMainAxisSize(
            measurables: List<IntrinsicMeasurable>,
            height: Int,
            arrangementSpacing: Int,
        ) = maxIntrinsicMainAxisSize(
            measurables,
            maxMainAxisIntrinsicItemSize,
            height,
            arrangementSpacing,
            maxItemsInMainAxis
        )

        fun intrinsicCrossAxisSize(
            measurables: List<IntrinsicMeasurable>,
            mainAxisAvailable: Int,
            mainAxisSpacing: Int,
            crossAxisSpacing: Int,
        ) = intrinsicCrossAxisSize(
            measurables,
            mainAxisSize = minMainAxisIntrinsicItemSize,
            crossAxisSize = minCrossAxisIntrinsicItemSize,
            mainAxisAvailable,
            mainAxisSpacing,
            crossAxisSpacing,
            maxItemsInMainAxis
        )

        val maxMainAxisIntrinsicItemSize: IntrinsicMeasurable.(Int, Int) -> Int =
            if (orientation == Orientation.HORIZONTAL) { _, h ->
                maxIntrinsicWidth(h)
            }
            else { _, w ->
                maxIntrinsicHeight(w)
            }
        val maxCrossAxisIntrinsicItemSize: IntrinsicMeasurable.(Int, Int) -> Int =
            if (orientation == Orientation.HORIZONTAL) { _, w ->
                maxIntrinsicHeight(w)
            }
            else { _, h ->
                maxIntrinsicWidth(h)
            }
        val minCrossAxisIntrinsicItemSize: IntrinsicMeasurable.(Int, Int) -> Int =
            if (orientation == Orientation.HORIZONTAL) { _, w ->
                minIntrinsicHeight(w)
            }
            else { _, h ->
                minIntrinsicWidth(h)
            }
        val minMainAxisIntrinsicItemSize: IntrinsicMeasurable.(Int, Int) -> Int =
            if (orientation == Orientation.HORIZONTAL) { _, h ->
                minIntrinsicWidth(h)
            }
            else { _, w ->
                minIntrinsicHeight(w)
            }
    }
}


private fun maxIntrinsicMainAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSize: IntrinsicMeasurable.(Int, Int) -> Int,
    crossAxisAvailable: Int,
    mainAxisSpacing: Int,
    maxItemsInMainAxis: Int,
): Int {
    var fixedSpace = 0
    var currentFixedSpace = 0
    var lastBreak = 0
    children.forEachIndexed { index, child ->
        val size = child.mainAxisSize(index, crossAxisAvailable) + mainAxisSpacing
        if (index + 1 - lastBreak == maxItemsInMainAxis || index + 1 == children.size) {
            lastBreak = index
            currentFixedSpace += size
            currentFixedSpace -= mainAxisSpacing // no mainAxisSpacing for last item in main axis
            fixedSpace = max(fixedSpace, currentFixedSpace)
            currentFixedSpace = 0
        } else {
            currentFixedSpace += size
        }
    }
    return fixedSpace
}

/**
 * Slower algorithm but needed to determine the minimum main axis size
 * Uses a binary search to search different scenarios to see the minimum main axis size
 */
private fun minIntrinsicMainAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSize: IntrinsicMeasurable.(Int, Int) -> Int,
    crossAxisSize: IntrinsicMeasurable.(Int, Int) -> Int,
    crossAxisAvailable: Int,
    mainAxisSpacing: Int,
    crossAxisSpacing: Int,
    maxItemsInMainAxis: Int,
): Int {
    val mainAxisSizes = IntArray(children.size) { 0 }
    val crossAxisSizes = IntArray(children.size) { 0 }
    for (index in children.indices) {
        val child = children[index]
        val mainAxisItemSize = child.mainAxisSize(index, crossAxisAvailable)
        mainAxisSizes[index] = mainAxisItemSize
        crossAxisSizes[index] = child.crossAxisSize(index, mainAxisItemSize)
    }
    val maxMainAxisSize = mainAxisSizes.sum()
    var mainAxisUsed = maxMainAxisSize
    var crossAxisUsed = crossAxisSizes.maxOf { it }
    val minimumItemSize = mainAxisSizes.maxOf { it }
    var low = minimumItemSize
    var high = maxMainAxisSize
    while (low < high) {
        if (crossAxisUsed == crossAxisAvailable) {
            return mainAxisUsed
        }
        val mid = (low + high) / 2
        mainAxisUsed = mid
        crossAxisUsed = intrinsicCrossAxisSize(
            children,
            mainAxisSizes,
            crossAxisSizes,
            mainAxisUsed,
            mainAxisSpacing,
            crossAxisSpacing,
            maxItemsInMainAxis
        )
        if (crossAxisUsed == crossAxisAvailable) {
            return mainAxisUsed
        } else if (crossAxisUsed > crossAxisAvailable) {
            low = mid + 1
        } else {
            high = mid - 1
        }
    }
    return mainAxisUsed
}

/**
 * FlowRow: Intrinsic height (cross Axis) is based on a specified width
 * FlowColumn: Intrinsic width (crossAxis) based on a specified height
 */
private fun intrinsicCrossAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSizes: IntArray,
    crossAxisSizes: IntArray,
    mainAxisAvailable: Int,
    mainAxisSpacing: Int,
    crossAxisSpacing: Int,
    maxItemsInMainAxis: Int,
): Int {
    return intrinsicCrossAxisSize(
        children,
        { index, _ -> mainAxisSizes[index] },
        { index, _ -> crossAxisSizes[index] },
        mainAxisAvailable,
        mainAxisSpacing,
        crossAxisSpacing,
        maxItemsInMainAxis
    )
}

/** FlowRow: Intrinsic height (cross Axis) is based on a specified width
 ** FlowColumn: Intrinsic width (crossAxis) based on a specified height
 */
private fun intrinsicCrossAxisSize(
    children: List<IntrinsicMeasurable>,
    mainAxisSize: IntrinsicMeasurable.(Int, Int) -> Int,
    crossAxisSize: IntrinsicMeasurable.(Int, Int) -> Int,
    mainAxisAvailable: Int,
    mainAxisSpacing: Int,
    crossAxisSpacing: Int,
    maxItemsInMainAxis: Int,
): Int {
    if (children.isEmpty()) {
        return 0
    }
    var nextChild = children.getOrNull(0)
    var nextCrossAxisSize = nextChild?.crossAxisSize(0, mainAxisAvailable) ?: 0
    var nextMainAxisSize = nextChild?.mainAxisSize(0, nextCrossAxisSize) ?: 0
    var remaining = mainAxisAvailable
    var currentCrossAxisSize = 0
    var totalCrossAxisSize = 0
    var lastBreak = 0
    children.forEachIndexed { index, _ ->
        nextChild!!
        val childCrossAxisSize = nextCrossAxisSize
        val childMainAxisSize = nextMainAxisSize
        remaining -= childMainAxisSize
        currentCrossAxisSize = maxOf(currentCrossAxisSize, childCrossAxisSize)
        // look ahead to simplify logic
        nextChild = children.getOrNull(index + 1)
        nextCrossAxisSize = nextChild?.crossAxisSize(index + 1, mainAxisAvailable) ?: 0
        nextMainAxisSize = nextChild?.mainAxisSize(index + 1, nextCrossAxisSize)
            ?.plus(mainAxisSpacing) ?: 0
        if (remaining < 0 || index + 1 == children.size ||
            (index + 1) - lastBreak == maxItemsInMainAxis ||
            remaining - nextMainAxisSize < 0
        ) {
            totalCrossAxisSize += currentCrossAxisSize + crossAxisSpacing
            currentCrossAxisSize = 0
            remaining = mainAxisAvailable
            lastBreak = index + 1
            nextMainAxisSize -= mainAxisSpacing
        }
    }
    // remove the last spacing for the last row or column
    totalCrossAxisSize -= crossAxisSpacing
    return totalCrossAxisSize
}

internal fun Measurable.mainAxisMin(orientation: Orientation, crossAxisSize: Int) =
    if (orientation == Orientation.HORIZONTAL) {
        minIntrinsicWidth(crossAxisSize)
    } else {
        minIntrinsicHeight(crossAxisSize)
    }

// We measure and cache to improve performance dramatically, instead of using intrinsics
// This only works so far for fixed size items.
// For weighted items, we continue to use their intrinsic widths.
// This is because their fixed sizes are only determined after we determine
// the number of items that can fit in the row/column it only lies on.
private fun Measurable.measureAndCache(
    constraints: AxisConstraints,
    orientation: Orientation,
    storePlaceable: (Placeable?) -> Unit,
): Int {
    val itemSize: Int = if ((containerData?.weight ?: 0) == 0) {
        // fixed sizes: measure once
        val placeable = measure(
            constraints.copy(
                mainAxisMin = 0,
            ).toConstraints(orientation)
        ).also(storePlaceable)
        placeable.mainAxisSize(orientation)
    } else {
        mainAxisMin(orientation, Constraints.Infinity)
    }
    return itemSize
}

private fun breakDownItems(
    measureHelper: RowColumnMeasurementHelper,
    orientation: Orientation,
    constraints: AxisConstraints,
    maxItemsInMainAxis: Int,
): FlowResult {
    val items = mutableVectorOf<RowColumnMeasureHelperResult>()
    val mainAxisMax = constraints.mainAxisMax
    val mainAxisMin = constraints.mainAxisMin
    val crossAxisMax = constraints.crossAxisMax
    val measurables: List<Measurable> = measureHelper.measurables
    val placeables: Array<Placeable?> = measureHelper.placeables

    val spacing = measureHelper.arrangementSpacing // arrangement spacing
    val subsetConstraints = AxisConstraints(
        mainAxisMin,
        mainAxisMax,
        0,
        crossAxisMax
    )
    // nextSize of the list, pre-calculated
    var nextSize: Int? = measurables.getOrNull(0)?.measureAndCache(
        subsetConstraints, orientation
    ) { placeable ->
        placeables[0] = placeable
    }

    var startBreakLineIndex = 0
    val endBreakLineList = arrayOfNulls<Int>(measurables.size)
    var endBreakLineIndex = 0

    var leftOver = mainAxisMax
    // figure out the mainAxisTotalSize which will be minMainAxis when measuring the row/column
    var mainAxisTotalSize = mainAxisMin
    var currentLineMainAxisSize = 0
    for (index in measurables.indices) {
        val itemMainAxisSize = nextSize!!
        currentLineMainAxisSize += itemMainAxisSize
        leftOver -= itemMainAxisSize
        nextSize = measurables.getOrNull(index + 1)?.measureAndCache(
            subsetConstraints, orientation
        ) { placeable ->
            placeables[index + 1] = placeable
        }?.plus(spacing)
        if (index + 1 >= measurables.size ||
            (index + 1) - startBreakLineIndex >= maxItemsInMainAxis ||
            leftOver - (nextSize ?: 0) < 0
        ) {
            mainAxisTotalSize = maxOf(mainAxisTotalSize, currentLineMainAxisSize)
            mainAxisTotalSize = minOf(mainAxisTotalSize, mainAxisMax)
            currentLineMainAxisSize = 0
            leftOver = mainAxisMax
            startBreakLineIndex = index + 1
            endBreakLineList[endBreakLineIndex] = index + 1
            endBreakLineIndex++
            // only add spacing for next items in the row or column, not the starting indexes
            nextSize = nextSize?.minus(spacing)
        }
    }

    val subsetBoxConstraints = subsetConstraints.copy(
        mainAxisMin = mainAxisTotalSize
    ).toConstraints(orientation)

    startBreakLineIndex = 0
    var crossAxisTotalSize = 0

    endBreakLineIndex = 0
    var endIndex = endBreakLineList.getOrNull(endBreakLineIndex)
    while (endIndex != null) {
        val result = measureHelper.measureWithoutPlacing(
            subsetBoxConstraints,
            startBreakLineIndex,
            endIndex
        )
        crossAxisTotalSize += result.crossAxisSize
        mainAxisTotalSize = maxOf(mainAxisTotalSize, result.mainAxisSize)
        items.add(
            result
        )
        startBreakLineIndex = endIndex
        endBreakLineIndex++
        endIndex = endBreakLineList.getOrNull(endBreakLineIndex)
    }

    crossAxisTotalSize = maxOf(crossAxisTotalSize, constraints.crossAxisMin)
    mainAxisTotalSize = maxOf(mainAxisTotalSize, constraints.mainAxisMin)
    return FlowResult(
        mainAxisTotalSize,
        crossAxisTotalSize,
        items,
    )
}

/**
 * FlowResult when broken down to multiple rows or columns based on [breakDownItems] algorithm
 *
 * @param mainAxisTotalSize the total size of the main axis
 * @param crossAxisTotalSize the total size of the cross axis when taken into account
 * the cross axis sizes of all items
 * @param items the row or column measurements for each row or column
 */
internal class FlowResult(
    val mainAxisTotalSize: Int,
    val crossAxisTotalSize: Int,
    val items: MutableVector<RowColumnMeasureHelperResult>,
)
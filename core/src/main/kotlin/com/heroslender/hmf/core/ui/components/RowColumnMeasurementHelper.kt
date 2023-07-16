package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.components.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign


/**
 * This is a data class that holds the determined width, height of a row,
 * and information on how to retrieve main axis and cross axis positions.
 */
internal class RowColumnMeasureHelperResult(
    val crossAxisSize: Int,
    val mainAxisSize: Int,
    val startIndex: Int,
    val endIndex: Int,
    val mainAxisPositions: IntArray,
)

internal class RowColumnMeasurementHelper(
    val orientation: Orientation,
    val arrangement: Arrangement,
    val arrangementSpacing: Int,
    val crossAxisAlignment: Alignment.Directional,
    val measurables: List<Measurable>,
    val placeables: Array<Placeable?>,
) {
    fun Placeable.mainAxisSize() =
        if (orientation == Orientation.HORIZONTAL) width else height

    fun Placeable.crossAxisSize() =
        if (orientation == Orientation.HORIZONTAL) height else width

    /**
     * Measures the row and column without placing, useful for reusing row/column logic
     *
     * @param measureScope The measure scope to retrieve density
     * @param constraints The desired constraints for the startIndex and endIndex
     * can hold null items if not measured.
     * @param startIndex The startIndex (inclusive) when examining measurables, placeable
     * and parentData
     * @param endIndex The ending index (exclusive) when examinning measurable, placeable
     * and parentData
     */
    fun measureWithoutPlacing(
        constraints: Constraints,
        startIndex: Int,
        endIndex: Int,
    ): RowColumnMeasureHelperResult {
        val axisConstraints = constraints.toAxisConstraints(orientation)
        val datas = Array(measurables.size) { i -> measurables[i].containerData }

        var totalWeight = 0
        var weightCount = 0

        val subSize = endIndex - startIndex

        var usedSize = 0
        var crossAxisUsedSize = 0
        var spaceAfterLastNoWeight = 0
        for (index in startIndex until endIndex) {
            val measurable = measurables[index]
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
                spaceAfterLastNoWeight = min(
                    arrangementSpacing,
                    axisConstraints.mainAxisMax - usedSize - placeable.mainAxisSize()
                )
                usedSize += placeable.mainAxisSize(orientation) + spaceAfterLastNoWeight
                crossAxisUsedSize = max(crossAxisUsedSize, placeable.crossAxisSize(orientation))
                placeables[index] = placeable
            }
        }

        if (weightCount > 0) {
            val freeSize = axisConstraints.mainAxisMax - usedSize - arrangementSpacing * (weightCount - 1)
            val weightUnitSize = freeSize / totalWeight
            var remainder = freeSize - datas.sumOf { it.weight * weightUnitSize }

            for (i in startIndex until endIndex) {
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

        val mainAxisPositions = IntArray(subSize) { 0 }
        val childrenMainAxisSize = IntArray(subSize) { index ->
            placeables[index + startIndex]!!.mainAxisSize()
        }

        return RowColumnMeasureHelperResult(
            mainAxisSize = mainAxisSize,
            crossAxisSize = crossAxisSize,
            startIndex = startIndex,
            endIndex = endIndex,
            mainAxisPositions = mainAxisPositions(
                mainAxisSize,
                childrenMainAxisSize,
                mainAxisPositions,
            )
        )
    }

    private fun mainAxisPositions(
        mainAxisLayoutSize: Int,
        childrenMainAxisSize: IntArray,
        mainAxisPositions: IntArray,
    ): IntArray {
        arrangement.arrange(
            mainAxisLayoutSize,
            childrenMainAxisSize,
            mainAxisPositions
        )
        return mainAxisPositions
    }

    private fun getCrossAxisPosition(
        placeable: Placeable,
        parentData: ContainerData?,
        crossAxisLayoutSize: Int,
    ): Int {
//        val childCrossAlignment = parentData?.crossAxisAlignment ?: crossAxisAlignment
        val childCrossAlignment = crossAxisAlignment
        return childCrossAlignment.align(
            size = crossAxisLayoutSize - placeable.crossAxisSize(),
        )
    }

    fun placeHelper(
        measureResult: RowColumnMeasureHelperResult,
        crossAxisOffset: Int,
    ) {
        for (i in measureResult.startIndex until measureResult.endIndex) {
            val placeable = placeables[i]
            placeable!!
            val mainAxisPositions = measureResult.mainAxisPositions
            val crossAxisPosition = getCrossAxisPosition(
                placeable,
                (measurables[i].parentData as? ContainerData),
                measureResult.crossAxisSize,
            ) + crossAxisOffset
            if (orientation == Orientation.HORIZONTAL) {
                placeable.placeAt(
                    mainAxisPositions[i - measureResult.startIndex],
                    crossAxisPosition
                )
            } else {
                placeable.placeAt(
                    crossAxisPosition,
                    mainAxisPositions[i - measureResult.startIndex]
                )
            }
        }
    }
}
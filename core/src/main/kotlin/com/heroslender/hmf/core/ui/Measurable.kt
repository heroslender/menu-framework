package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.type.MeasuringIntrinsics

interface IntrinsicMeasurable {

    val parentData: Any?

    /**
     * Calculates the minimum width that the layout can be such that
     * the content of the layout will be painted correctly.
     */
    fun minIntrinsicWidth(height: Int): Int

    /**
     * Calculates the smallest width beyond which increasing the width never
     * decreases the height.
     */
    fun maxIntrinsicWidth(height: Int): Int

    /**
     * Calculates the minimum height that the layout can be such that
     * the content of the layout will be painted correctly.
     */
    fun minIntrinsicHeight(width: Int): Int

    /**
     * Calculates the smallest height beyond which increasing the height never
     * decreases the width.
     */
    fun maxIntrinsicHeight(width: Int): Int
}

interface Measurable : IntrinsicMeasurable {

    fun measure(constraints: Constraints): Placeable
}

interface MeasureScope : IntrinsicMeasureScope {
    interface MeasureResult {
        val width: Int
        val height: Int

        fun placeChildren()
    }

    companion object : MeasureScope
}

interface IntrinsicMeasureScope

inline fun MeasureScope.layout(width: Int, height: Int, crossinline placeChild: () -> Unit = {}) =
    object : MeasureScope.MeasureResult {
        override val width: Int = width
        override val height: Int = height

        override fun placeChildren() {
            placeChild()
        }
    }

interface MeasurableGroup {
    fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult

    /**
     * The function used to calculate [IntrinsicMeasurable.minIntrinsicWidth]. It represents
     * the minimum width this layout can take, given a specific height, such that the content
     * of the layout can be painted correctly.
     */
    fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ): Int {
        val mapped = measurables.map {
            MeasuringIntrinsics.DefaultIntrinsicMeasurable(it,
                MeasuringIntrinsics.IntrinsicMinMax.Min,
                MeasuringIntrinsics.IntrinsicWidthHeight.Width)
        }
        val constraints = Constraints(maxHeight = height)
        val layoutResult = MeasureScope.measure(mapped, constraints)
        return layoutResult.width
    }

    /**
     * The function used to calculate [IntrinsicMeasurable.minIntrinsicHeight]. It represents
     * defines the minimum height this layout can take, given  a specific width, such
     * that the content of the layout will be painted correctly.
     */
    fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int {
        val mapped = measurables.map {
            MeasuringIntrinsics.DefaultIntrinsicMeasurable(it,
                MeasuringIntrinsics.IntrinsicMinMax.Min,
                MeasuringIntrinsics.IntrinsicWidthHeight.Height)
        }
        val constraints = Constraints(maxWidth = width)
        val layoutResult = MeasureScope.measure(mapped, constraints)
        return layoutResult.height
    }

    /**
     * The function used to calculate [IntrinsicMeasurable.maxIntrinsicWidth]. It represents the
     * minimum width such that increasing it further will not decrease the minimum intrinsic height.
     */
    fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ): Int {
        val mapped = measurables.map {
            MeasuringIntrinsics.DefaultIntrinsicMeasurable(it,
                MeasuringIntrinsics.IntrinsicMinMax.Max,
                MeasuringIntrinsics.IntrinsicWidthHeight.Width)
        }
        val constraints = Constraints(maxHeight = height)
        val layoutResult = MeasureScope.measure(mapped, constraints)
        return layoutResult.width
    }

    /**
     * The function used to calculate [IntrinsicMeasurable.maxIntrinsicHeight]. It represents the
     * minimum height such that increasing it further will not decrease the minimum intrinsic width.
     */
    fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int {
        val mapped = measurables.map {
            MeasuringIntrinsics.DefaultIntrinsicMeasurable(it,
                MeasuringIntrinsics.IntrinsicMinMax.Max,
                MeasuringIntrinsics.IntrinsicWidthHeight.Height)
        }
        val constraints = Constraints(maxWidth = width)
        val layoutResult = MeasureScope.measure(mapped, constraints)
        return layoutResult.height
    }

    companion object : MeasurableGroup {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints,
        ): MeasureScope.MeasureResult = when {
            measurables.isEmpty() -> layout(constraints.minWidth, constraints.minHeight) {}
            measurables.size == 1 -> {
                val placeable = measurables[0].measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeAt(0, 0)
                }
            }
            else -> {
                val placeables = measurables.map {
                    it.measure(constraints)
                }
                val maxWidth = placeables.maxOf { it.width }
                val maxHeight = placeables.maxOf { it.height }
                layout(maxWidth, maxHeight) {
                    placeables.forEach { placeable ->
                        placeable.placeAt(0, 0)
                    }
                }
            }
        }
    }
}
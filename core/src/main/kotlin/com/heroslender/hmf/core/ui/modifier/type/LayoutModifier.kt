package com.heroslender.hmf.core.ui.modifier.type

import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier

interface LayoutModifier : Modifier.Element {

    fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureScope.MeasureResult

    /**
     * The function used to calculate [IntrinsicMeasurable.minIntrinsicWidth].
     */
    fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int = MeasuringIntrinsics.minWidth(
        this@LayoutModifier,
        measurable,
        height
    )

    /**
     * The lambda used to calculate [IntrinsicMeasurable.minIntrinsicHeight].
     */
    fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int = MeasuringIntrinsics.minHeight(
        this@LayoutModifier,
        measurable,
        width
    )

    /**
     * The function used to calculate [IntrinsicMeasurable.maxIntrinsicWidth].
     */
    fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int = MeasuringIntrinsics.maxWidth(
        this@LayoutModifier,
        measurable,
        height
    )

    /**
     * The lambda used to calculate [IntrinsicMeasurable.maxIntrinsicHeight].
     */
    fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int = MeasuringIntrinsics.maxHeight(
        this@LayoutModifier,
        measurable,
        width
    )
}

internal object MeasuringIntrinsics {
    internal fun minWidth(
        modifier: LayoutModifier,
        intrinsicMeasurable: IntrinsicMeasurable,
        h: Int,
    ): Int {
        val measurable = DefaultIntrinsicMeasurable(
            intrinsicMeasurable,
            IntrinsicMinMax.Min,
            IntrinsicWidthHeight.Width
        )
        val constraints = Constraints(maxHeight = h)
        val layoutResult = with(modifier) {
            MeasureScope.measure(measurable, constraints)
        }
        return layoutResult.width
    }

    internal fun minHeight(
        modifier: LayoutModifier,
        intrinsicMeasurable: IntrinsicMeasurable,
        w: Int,
    ): Int {
        val measurable = DefaultIntrinsicMeasurable(
            intrinsicMeasurable,
            IntrinsicMinMax.Min,
            IntrinsicWidthHeight.Height
        )
        val constraints = Constraints(maxWidth = w)
        val layoutResult = with(modifier) {
            MeasureScope.measure(measurable, constraints)
        }
        return layoutResult.height
    }

    internal fun maxWidth(
        modifier: LayoutModifier,
        intrinsicMeasurable: IntrinsicMeasurable,
        h: Int,
    ): Int {
        val measurable = DefaultIntrinsicMeasurable(
            intrinsicMeasurable,
            IntrinsicMinMax.Max,
            IntrinsicWidthHeight.Width
        )
        val constraints = Constraints(maxHeight = h)
        val layoutResult = with(modifier) {
            MeasureScope.measure(measurable, constraints)
        }
        return layoutResult.width
    }

    internal fun maxHeight(
        modifier: LayoutModifier,
        intrinsicMeasurable: IntrinsicMeasurable,
        w: Int,
    ): Int {
        val measurable = DefaultIntrinsicMeasurable(
            intrinsicMeasurable,
            IntrinsicMinMax.Max,
            IntrinsicWidthHeight.Height
        )
        val constraints = Constraints(maxWidth = w)
        val layoutResult = with(modifier) {
            MeasureScope.measure(measurable, constraints)
        }
        return layoutResult.height
    }

    internal class DefaultIntrinsicMeasurable(
        val measurable: IntrinsicMeasurable,
        val minMax: IntrinsicMinMax,
        val widthHeight: IntrinsicWidthHeight,
    ) : Measurable {
        override val parentData: Any?
            get() = measurable.parentData

        override fun measure(constraints: Constraints): Placeable {
            if (widthHeight == IntrinsicWidthHeight.Width) {
                val width = if (minMax == IntrinsicMinMax.Max) {
                    measurable.maxIntrinsicWidth(constraints.maxHeight)
                } else {
                    measurable.minIntrinsicWidth(constraints.maxHeight)
                }
                return EmptyPlaceable(width, constraints.maxHeight)
            }
            val height = if (minMax == IntrinsicMinMax.Max) {
                measurable.maxIntrinsicHeight(constraints.maxWidth)
            } else {
                measurable.minIntrinsicHeight(constraints.maxWidth)
            }
            return EmptyPlaceable(constraints.maxWidth, height)
        }

        override fun minIntrinsicWidth(height: Int): Int {
            return measurable.minIntrinsicWidth(height)
        }

        override fun maxIntrinsicWidth(height: Int): Int {
            return measurable.maxIntrinsicWidth(height)
        }

        override fun minIntrinsicHeight(width: Int): Int {
            return measurable.minIntrinsicHeight(width)
        }

        override fun maxIntrinsicHeight(width: Int): Int {
            return measurable.maxIntrinsicHeight(width)
        }
    }

    private class EmptyPlaceable(override val width: Int, override val height: Int) : Placeable {
        override var isVisible: Boolean = false

        override fun placeAt(x: Int, y: Int) {
        }
    }

    internal enum class IntrinsicMinMax { Min, Max }
    internal enum class IntrinsicWidthHeight { Width, Height }
}

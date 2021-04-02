package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable
import kotlin.math.max

interface LayoutModifier : Modifier.Element {

    fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureScope.MeasureResult
}

interface MeasurableGroup {
    fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult

    companion object : MeasurableGroup {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints,
        ): MeasureScope.MeasureResult = when {
            measurables.isEmpty() -> result(0, 0) {}
            measurables.size == 1 -> {
                val placeable = measurables[0].measure(constraints)
                result(placeable.width, placeable.height) {
                    placeable.placeAt(0, 0)
                }
            }
            else -> {
                val placeables = measurables.map {
                    it.measure(constraints)
                }
                val maxWidth = placeables.maxOf { it.width }
                val maxHeight = placeables.maxOf { it.height }
                result(maxWidth, maxHeight) {
                    placeables.forEach { placeable ->
                        placeable.placeAt(0, 0)
                    }
                }
            }
        }
    }
}

interface Measurable {
    fun measure(constraints: Constraints): Placeable
}

interface MeasureScope {
    interface MeasureResult {
        val width: Int
        val height: Int

        fun placeChildren()
    }

    fun result(width: Int, height: Int, placeChild: () -> Unit) = object : MeasureResult {
        override val width: Int = width
        override val height: Int = height

        override fun placeChildren() {
            placeChild()
        }
    }

    companion object : MeasureScope
}


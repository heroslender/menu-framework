package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.ui.modifier.Constraints

interface Measurable {

    val data: Any?

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
            measurables.isEmpty() -> result(constraints.minWidth, constraints.minHeight) {}
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
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

    companion object : MeasureScope
}

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
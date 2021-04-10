package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.*
import com.heroslender.hmf.core.ui.modifier.type.DrawerModifier
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.layout


fun Modifier.border(
    color: IColor,
    thickness: Int = 1,
    radius: Int = 0,
): Modifier = border(
    thickness = thickness,
    color = color,
    topLeft = radius,
    topRight = radius,
    bottomRight = radius,
    bottomLeft = radius,
)

fun Modifier.border(
    color: IColor,
    thickness: Int = 1,
    topLeft: Int = 0,
    topRight: Int = 0,
    bottomRight: Int = 0,
    bottomLeft: Int = 0,
): Modifier = border(
    thickness = thickness,
    color = color,
    topLeft = BorderRadiusDrawer.of(topLeft).topLeft,
    topRight = BorderRadiusDrawer.of(topRight).topRight,
    bottomRight = BorderRadiusDrawer.of(bottomRight).bottomRight,
    bottomLeft = BorderRadiusDrawer.of(bottomLeft).bottomLeft,
)

fun Modifier.border(
    color: IColor,
    thickness: Int,
    topLeft: BorderRadius,
    topRight: BorderRadius,
    bottomRight: BorderRadius,
    bottomLeft: BorderRadius,
): Modifier {
    if (color == IColor.TRANSPARENT ||
            thickness == 0
//        (topLeft.radius == 0 && topRight.radius == 0 && bottomLeft.radius == 0 && bottomRight.radius == 0)
    ) {
        return this
    }

    return this then borderDrawer(
        thickness = thickness,
        color = color,
        topLeft = topLeft,
        topRight = topRight,
        bottomRight = bottomRight,
        bottomLeft = bottomLeft,
        inner = this.any { it is BorderDrawer }
    )
}

fun borderDrawer(
    thickness: Int,
    color: IColor,
    radius: Int = 0,
    inner: Boolean = false,
): BorderDrawer = borderDrawer(
    thickness = thickness,
    color = color,
    topLeft = radius,
    topRight = radius,
    bottomRight = radius,
    bottomLeft = radius,
    inner = inner
)

fun borderDrawer(
    thickness: Int,
    color: IColor,
    topLeft: Int = 0,
    topRight: Int = 0,
    bottomRight: Int = 0,
    bottomLeft: Int = 0,
    inner: Boolean = false,
): BorderDrawer = borderDrawer(
    thickness = thickness,
    color = color,
    topLeft = BorderRadiusDrawer.of(topLeft).topLeft,
    topRight = BorderRadiusDrawer.of(topRight).topRight,
    bottomRight = BorderRadiusDrawer.of(bottomRight).bottomRight,
    bottomLeft = BorderRadiusDrawer.of(bottomLeft).bottomLeft,
    inner = inner
)

fun borderDrawer(
    thickness: Int,
    color: IColor,
    topLeft: BorderRadius,
    topRight: BorderRadius,
    bottomRight: BorderRadius,
    bottomLeft: BorderRadius,
    inner: Boolean = false,
): BorderDrawer = BorderDrawer(
    thickness = thickness,
    color = color,
    topLeft = topLeft,
    topRight = topRight,
    bottomRight = bottomRight,
    bottomLeft = bottomLeft,
    inner = inner
)

class BorderDrawer(
    val thickness: Int,
    val color: IColor,
    val topLeft: BorderRadius,
    val topRight: BorderRadius,
    val bottomRight: BorderRadius,
    val bottomLeft: BorderRadius,
    val inner: Boolean,
) : DrawerModifier, LayoutModifier {
    companion object {
        val None: BorderDrawer = BorderDrawer(
            0,
            IColor.TRANSPARENT,
            BorderRadiusDrawer.of(0).topLeft,
            BorderRadiusDrawer.of(0).topRight,
            BorderRadiusDrawer.of(0).bottomRight,
            BorderRadiusDrawer.of(0).bottomLeft,
            false
        )
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureScope.MeasureResult {
        val offset = thickness * 2
        val placeable = measurable.measure(constraints.offset(-offset, -offset))

        val width = constraints.constrainWidth(placeable.width + offset)
        val height = constraints.constrainHeight(placeable.height + offset)
        return layout(width, height) {
            placeable.placeAt(thickness, thickness)
        }
    }

    override fun Placeable.onDraw(canvas: Canvas) {
        if (this@BorderDrawer === None) {
            return
        }

        val width = this.width
        val height = this.height

        var drawn: Boolean
        for (depth in 0 until thickness) {
            // Draw top-left corner
            for (x in depth until topLeft.radius + depth) {
                drawn = false
                for (y in depth until topLeft.radius) {
                    if (topLeft.isBorder(x - depth, y - depth)) {
                        canvas.setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        canvas.setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            val rightEnd = width - depth
            var rightStart = rightEnd - topRight.radius

            for (x in topLeft.radius + depth until rightStart) {
                canvas.setPixel(x, depth, color)
            }

            // Draw top-right corner
            for (x in rightStart until rightEnd) {
                drawn = false
                for (y in depth until topRight.radius + depth) {
                    if (topRight.isBorder(x - rightStart, y - depth)) {
                        canvas.setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        canvas.setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            val bottomEnd = height - depth
            var bottomStart = bottomEnd - bottomLeft.radius

            for (y in topLeft.radius + depth until bottomStart) {
                canvas.setPixel(depth, y, color)
            }

            // Draw bottom-left corner
            for (x in depth until bottomLeft.radius + depth) {
                drawn = false
                for (y in bottomEnd - 1 downTo bottomStart) {
                    if (bottomLeft.isBorder(x - depth, y - bottomStart)) {
                        canvas.setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        canvas.setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            rightStart = rightEnd - bottomRight.radius
            bottomStart = bottomEnd - bottomRight.radius

            for (x in bottomLeft.radius + depth until rightStart) {
                canvas.setPixel(x, height - depth - 1, color)
            }

            // Draw bottom-right corner
            for (x in rightStart until rightEnd) {
                drawn = false
                for (y in bottomEnd - 1 downTo bottomStart) {
                    if (bottomRight.isBorder(x - rightStart, y - bottomStart)) {
                        canvas.setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        canvas.setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            for (y in topRight.radius + depth until bottomStart) {
                canvas.setPixel(width - depth - 1, y, color)
            }
        }
    }
}

class BorderRadius(
    val radius: Int,
    val pixels: BooleanArray,
) {
    fun isBorder(x: Int, y: Int): Boolean {
        return x in 0 until radius && y in 0 until radius && pixels[x + y * radius]
    }
}

class BorderRadiusDrawer(
    private val radius: Int,
    topLeft: BooleanArray,
) {
    init {
        if (topLeft.size != radius * radius) {
            error("Invalid border radius data array! Radius of $radius for array with size ${topLeft.size}")
        }
    }

    val topLeft: BorderRadius = BorderRadius(radius, topLeft)
    val topRight: BorderRadius = BorderRadius(radius, mirrorHorizontally(topLeft))
    val bottomLeft: BorderRadius = BorderRadius(radius, mirrorVertically(topLeft))
    val bottomRight: BorderRadius = BorderRadius(radius, mirrorVertically(topRight.pixels))

    private fun mirrorHorizontally(source: BooleanArray): BooleanArray {
        if (radius <= 1) {
            return booleanArrayOf(*source)
        }

        val copy = BooleanArray(source.size)
        val width = radius - 1
        source.forEachIndexed { i, d ->
            val x: Int = i % radius
            val y: Int = i / radius

            copy[(width - x) + y * radius] = d
        }

        return copy
    }

    private fun mirrorVertically(source: BooleanArray): BooleanArray {
        if (radius <= 1) {
            return booleanArrayOf(*source)
        }

        val copy = BooleanArray(source.size)
        val width = radius - 1
        source.forEachIndexed { i, d ->
            val x: Int = i % radius
            val y: Int = i / radius

            copy[x + (width - y) * radius] = d
        }

        return copy
    }

    companion object {
        fun of(radius: Int): BorderRadiusDrawer = when (radius) {
            0 -> ZERO
            1 -> ONE
            2 -> TWO
            3 -> THREE
            4 -> FOUR
            else -> FIVE
        }

        private val ZERO = BorderRadiusDrawer(0, booleanArrayOf())

        private val ONE = BorderRadiusDrawer(1, booleanArrayOf(false))

        private val TWO = BorderRadiusDrawer(2, booleanArrayOf(
            false, false,
            false, true
        ))

        private val THREE = BorderRadiusDrawer(3, booleanArrayOf(
            false, false, false,
            false, false, true,
            false, true, false,
        ))

        private val FOUR = BorderRadiusDrawer(4, booleanArrayOf(
            false, false, false, false,
            false, false, true, true,
            false, true, false, false,
            false, true, false, false,
        ))

        private val FIVE = BorderRadiusDrawer(5, booleanArrayOf(
            false, false, false, false, false,
            false, false, false, true, true,
            false, false, true, false, false,
            false, true, false, false, false,
            false, true, false, false, false,
        ))
    }
}

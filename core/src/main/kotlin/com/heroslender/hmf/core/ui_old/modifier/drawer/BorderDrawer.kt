package com.heroslender.hmf.core.ui_old.modifier.drawer

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui_old.Component
import com.heroslender.hmf.core.ui_old.DrawFunc

class BorderDrawer(
    val thickness: Int,
    val color: IColor,
    val topLeft: BorderRadius,
    val topRight: BorderRadius,
    val bottomRight: BorderRadius,
    val bottomLeft: BorderRadius,
    val inner: Boolean,
) : Drawer {
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

    override fun Component.onDraw(setPixel: DrawFunc) {
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
                        setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            val rightEnd = width - depth
            var rightStart = rightEnd - topRight.radius

            for (x in topLeft.radius + depth until rightStart) {
                setPixel(x, depth, color)
            }

            // Draw top-right corner
            for (x in rightStart until rightEnd) {
                drawn = false
                for (y in depth until topRight.radius + depth) {
                    if (topRight.isBorder(x - rightStart, y - depth)) {
                        setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            val bottomEnd = height - depth
            var bottomStart = bottomEnd - bottomLeft.radius

            for (y in topLeft.radius + depth until bottomStart) {
                setPixel(depth, y, color)
            }

            // Draw bottom-left corner
            for (x in depth until bottomLeft.radius + depth) {
                drawn = false
                for (y in bottomEnd - 1 downTo bottomStart) {
                    if (bottomLeft.isBorder(x - depth, y - bottomStart)) {
                        setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            rightStart = rightEnd - bottomRight.radius
            bottomStart = bottomEnd - bottomRight.radius

            for (x in bottomLeft.radius + depth until rightStart) {
                setPixel(x, height - depth - 1, color)
            }

            // Draw bottom-right corner
            for (x in rightStart until rightEnd) {
                drawn = false
                for (y in bottomEnd - 1 downTo bottomStart) {
                    if (bottomRight.isBorder(x - rightStart, y - bottomStart)) {
                        setPixel(x, y, color)
                        drawn = true
                    } else if (!inner && depth == 0 && !drawn) {
                        setPixel(x, y, IColor.TRANSPARENT)
                    }
                }
            }

            for (y in topRight.radius + depth until bottomStart) {
                setPixel(width - depth - 1, y, color)
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

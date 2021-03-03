package com.heroslender.hmf.bukkit.map

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor

class MapCanvas(
    override val width: Int,
    override val height: Int,
) : Canvas {
    private val buffer: ByteArray = ByteArray(width * height)

    override fun setPixel(x: Int, y: Int, color: IColor) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            buffer[x + y * width] = color.id
        }
    }

    fun getPixel(x: Int, y: Int): Byte {
        return if (x >= 0 && y >= 0 && x < width && y < height)
            buffer[x + y * width]
        else
            0
    }

    operator fun get(x: Int, y: Int): Byte = getPixel(x, y)
}
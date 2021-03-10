package com.heroslender.hmf.bukkit.map

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import kotlin.math.min

open class MapCanvas(
    final override val width: Int,
    final override val height: Int,
) : Canvas {
    protected val buffer: ByteArray = ByteArray(width * height)

    override fun setPixel(x: Int, y: Int, color: IColor) = setPixelByte(x, y, color.id)

    override fun setPixelByte(x: Int, y: Int, color: Byte) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            buffer[x + y * width] = color
        }
    }

    override fun getPixel(x: Int, y: Int): IColor = throw UnsupportedOperationException("Not implemented")

    override fun getPixelByte(x: Int, y: Int): Byte {
        return if (x >= 0 && y >= 0 && x < width && y < height)
            buffer[x + y * width]
        else
            0
    }

    override fun draw(other: Canvas, offsetX: Int, offsetY: Int) {
        for (x in 0 until min(other.width, width)) {
            for (y in 0 until min(other.height, height)) {
                val color = other.getPixelByte(x, y)
                if (color != Color.TRANSPARENT.id) {
                    buffer[x + offsetX + (y + offsetY) * width] = color
                }
            }
        }
    }

    override fun newCanvas(width: Int, height: Int): Canvas = UnverifiedMapCanvas(width, height)

    operator fun get(x: Int, y: Int): Byte = getPixelByte(x, y)

    class UnverifiedMapCanvas(
        width: Int,
        height: Int,
    ) : MapCanvas(width, height) {

        override fun setPixelByte(x: Int, y: Int, color: Byte) {
            buffer[x + y * width] = color
        }

        override fun getPixelByte(x: Int, y: Int): Byte = buffer[x + y * width]
    }
}
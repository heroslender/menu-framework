package com.heroslender.hmf.bukkit.map

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import kotlin.math.min

open class MapCanvas(
    final override val width: Int,
    final override val height: Int,
    val buffer: ByteArray = ByteArray(width * height),
) : Canvas {
    var offsetX: Int = 0
    var offsetY: Int = 0

    constructor(other: MapCanvas) : this(other.width, other.height, other.buffer.clone())

    override fun clone(): Canvas {
        return MapCanvas(this)
    }

    override fun setPixel(x: Int, y: Int, color: IColor) = setPixelByte(x, y, color.id)

    override fun setPixelByte(x: Int, y: Int, color: Byte) {
        val x = x + offsetX
        val y = y + offsetY

        if (x >= 0 && y >= 0 && x < width && y < height) {
            buffer[x + y * width] = color
        }
    }

    override fun getPixel(x: Int, y: Int): IColor = throw UnsupportedOperationException("Not implemented")

    override fun getPixelByte(x: Int, y: Int): Byte {
        val x = x + offsetX
        val y = y + offsetY

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

    override fun resetOffset() {
        this.offsetX = 0
        this.offsetY = 0
    }

    override fun addOffset(x: Int, y: Int) {
        this.offsetX += x
        this.offsetY += y
    }

    override fun newCanvas(width: Int, height: Int): Canvas = UnverifiedMapCanvas(width, height)

    override fun subCanvas(width: Int, height: Int, offsetX: Int, offsetY: Int): Canvas {
        val canvas = UnverifiedMapCanvas(width, height)


        for (x in 0 until min(width, this.width - offsetX)) {
            for (y in 0 until min(height, this.height - offsetY)) {
                canvas.buffer[x + y * width] = buffer[x + offsetX + (y + offsetY) * this.width]
            }
        }

        return canvas
    }

    operator fun get(x: Int, y: Int): Byte = getPixelByte(x, y)

    class UnverifiedMapCanvas(
        width: Int,
        height: Int,
    ) : MapCanvas(width, height) {

        override fun setPixelByte(x: Int, y: Int, color: Byte) {
            buffer[x + offsetX + (y + offsetY) * width] = color
        }

        override fun getPixelByte(x: Int, y: Int): Byte = buffer[x + offsetX + (y + offsetY) * width]
    }
}
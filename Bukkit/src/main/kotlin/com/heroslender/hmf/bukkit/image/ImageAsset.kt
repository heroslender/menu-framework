@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package com.heroslender.hmf.bukkit.image

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.components.Image
import kotlin.math.min

class ImageAsset(
    asset: String,
    private val buffer: ByteArray,
    override val width: Int,
    override val height: Int = buffer.size / width,
) : Image {
    init {
        require(buffer.size == width * height) { "Buffer size is invalid for $asset!" }
    }

    override fun draw(canvas: Canvas, offsetX: Int, offsetY: Int) {
        for (x in 0 until min(canvas.width - offsetX, width)) {
            for (y in 0 until min(canvas.height - offsetY, height)) {
                val pixel = buffer[x + y * width]
                if (pixel != IColor.TRANSPARENT.id) {
                    canvas.setPixelByte(x + offsetX, y + offsetY, pixel)
                }
            }
        }
    }
}


@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package com.heroslender.hmf.bukkit.image

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.Placeable
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

    override fun Placeable.draw(canvas: Canvas) {
        val imageWidth = this@ImageAsset.width
        val imageHeight = this@ImageAsset.height

        for (x in 0 until min(imageWidth, width)) {
            for (y in 0 until min(imageHeight, height)) {
                val pixel = buffer[x + y * width]
                if (pixel != IColor.TRANSPARENT.id) {
                    canvas.setPixelByte(x, y, pixel)
                }
            }
        }
    }
}


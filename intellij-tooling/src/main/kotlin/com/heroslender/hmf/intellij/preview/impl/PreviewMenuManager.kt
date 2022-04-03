package com.heroslender.hmf.intellij.preview.impl

import com.heroslender.hmf.core.*
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.components.Image
import com.heroslender.hmf.core.utils.getResource
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.min

class PreviewMenuManager(private val classLoader: ClassLoader) : MenuManager<Menu> {
    override fun register(menu: Menu) {
//            TODO("Not yet implemented")
    }

    override fun unregister(menu: Menu) {
//            TODO("Not yet implemented")
    }

    override val imageProvider: ImageProvider = object : ImageProvider {
        override fun getImage(
            url: String,
            width: Int,
            height: Int,
            resizeMode: ImageProvider.ImageResizeMode,
            cached: Boolean,
        ): Image? {
            return getImageResource(url, width, height, resizeMode)
        }
    }

    override fun dispose() {
//            TODO("Not yet implemented")
    }

    private fun getImageResource(
        asset: String,
        width: Int,
        height: Int,
        resizeMode: ImageProvider.ImageResizeMode,
    ): Image? {
        val resource: InputStream = getResource(asset, this.classLoader) ?: return null
        var bImage: BufferedImage = ImageIO.read(resource)

        if (width > 0 || height > 0) {
            val newWidth: Int
            val newHeight: Int
            if (width > 0 && height > 0) {
                when (resizeMode) {
                    ImageProvider.ImageResizeMode.COVER -> {
                        val w = (height.toDouble() / bImage.height * bImage.width).toInt()
                        if (w < width) {
                            newWidth = width
                            newHeight = (width.toDouble() / bImage.width * bImage.height).toInt()
                        } else {
                            newWidth = w
                            newHeight = height
                        }
                    }
                    ImageProvider.ImageResizeMode.CONTAIN -> {
                        val w = (height.toDouble() / bImage.height * bImage.width).toInt()
                        if (w > width) {
                            newWidth = width
                            newHeight = (width.toDouble() / bImage.width * bImage.height).toInt()
                        } else {
                            newWidth = w
                            newHeight = height
                        }
                    }
                    ImageProvider.ImageResizeMode.STRETCH -> {
                        newWidth = (height.toDouble() / bImage.height * bImage.width).toInt()
                        newHeight = (width.toDouble() / bImage.width * bImage.height).toInt()
                    }
                }
            } else {
                newWidth = if (width > 0) width else (height.toDouble() / bImage.height * bImage.width).toInt()
                newHeight = if (height > 0) height else (width.toDouble() / bImage.width * bImage.height).toInt()
            }

            val resized = BufferedImage(newWidth, newHeight, 2)
            val graphics = resized.createGraphics()
            graphics.drawImage(bImage, 0, 0, newWidth, newHeight, null)
            graphics.dispose()
            bImage = resized
        }

        val rgb = bImage.getRGB(0, 0, bImage.width, bImage.height, null, 0, bImage.width)
        val buffer = JetpImageUtil.simplify(rgb)

        return ImageAsset(asset, buffer, bImage.width)
    }

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
                    val pixel = buffer[x + y * imageWidth]
                    if (pixel != IColor.TRANSPARENT.id) {
                        canvas.setPixelByte(x, y, pixel)
                    }
                }
            }
        }
    }
}
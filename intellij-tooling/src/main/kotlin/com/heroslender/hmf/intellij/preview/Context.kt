package com.heroslender.hmf.intellij.preview

import com.heroslender.hmf.core.*
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.components.Image
import com.heroslender.hmf.core.utils.getResource
import com.heroslender.hmf.intellij.insight.Color
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.min

class Context(
    override val canvas: ICanvas,
    override var root: Composable? = null,
    classLoader: ClassLoader,
) : RenderContext {
    override val manager: DummyManager = DummyManager(classLoader)
    override lateinit var menu: Menu

    private var callback: () -> Unit = {}

    override fun update() {
        callback()
    }

    override fun onUpdate(callback: () -> Unit) {
        this.callback = callback
    }

    class DummyManager(private val classLoader: ClassLoader) : MenuManager<Menu> {
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

    open class ICanvas(
        final override val width: Int,
        final override val height: Int,
        val buffer: ByteArray = ByteArray(width * height),
    ) : Canvas {
        var offsetX: Int = 0
        var offsetY: Int = 0

        constructor(other: ICanvas) : this(other.width, other.height, other.buffer.clone())

        override fun clone(): Canvas {
            return ICanvas(this)
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

        override fun newCanvas(width: Int, height: Int): Canvas = ICanvas(width, height)

        override fun subCanvas(width: Int, height: Int, offsetX: Int, offsetY: Int): Canvas {
            val canvas = ICanvas(width, height)


            for (x in 0 until min(width, this.width - offsetX)) {
                for (y in 0 until min(height, this.height - offsetY)) {
                    canvas.buffer[x + y * width] = buffer[x + offsetX + (y + offsetY) * this.width]
                }
            }

            return canvas
        }

        operator fun get(x: Int, y: Int): Byte = getPixelByte(x, y)
    }
}
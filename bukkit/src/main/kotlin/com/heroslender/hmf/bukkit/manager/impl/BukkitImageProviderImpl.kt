package com.heroslender.hmf.bukkit.manager.impl

import com.heroslender.hmf.bukkit.image.ImageAsset
import com.heroslender.hmf.bukkit.image.JetpImageUtil
import com.heroslender.hmf.core.ImageProvider
import com.heroslender.hmf.core.ui.components.Image
import com.heroslender.hmf.core.utils.getResource
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

class BukkitImageProviderImpl : ImageProvider {
    private val cache: MutableMap<String, Image> = mutableMapOf()

    override fun getImage(
        asset: String,
        width: Int,
        height: Int,
        resizeMode: ImageProvider.ImageResizeMode,
        cached: Boolean,
    ): Image? {
        return if (cached)
            getImageOrCompute(asset, width, height, resizeMode)
        else
            getImageResource(asset, width, height, resizeMode)
    }

    /**
     * Get an [Image] from the cache, or load it & cache.
     */
    private fun getImageOrCompute(
        asset: String,
        width: Int,
        height: Int,
        resizeMode: ImageProvider.ImageResizeMode,
    ): Image? {
        val key = getImageKey(asset, width, height)
        val current = cache[key]
        if (current == null) {
            val image = getImageResource(asset, width, height, resizeMode) ?: return null
            cache[key] = image
            return image
        }

        return current
    }

    /**
     * Get the key to be assigned to an image with the given properties.
     */
    private fun getImageKey(asset: String, width: Int, height: Int): String = "[${width}x$height]$asset"

    /**
     * Get an [Image] from the jar resources with the [asset] path.
     *
     * If [width] is specified and higher than zero, the image will be
     * resized to that width. The same applies to the [height].
     *
     * If one is specified without the other, the image will be resized
     * maintaining the same aspect ratio.
     */
    private fun getImageResource(
        asset: String,
        width: Int,
        height: Int,
        resizeMode: ImageProvider.ImageResizeMode,
    ): Image? {
        val resource: InputStream = getResource(asset) ?: return null
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
}
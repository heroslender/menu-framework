package com.heroslender.hmf.bukkit.image

import com.heroslender.hmf.core.ui.components.Image
import com.heroslender.hmf.core.utils.getResource
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

class ImageManager(
    private val cache: ImageCache = ImageCache(),
) {

    fun getImage(asset: String, width: Int, height: Int, cached: Boolean): Image? =
        if (cached)
            cache.getImageOrCompute(asset, width, height)
        else
            getImageResource(asset, width, height)

    class ImageCache(
        private val cache: MutableMap<String, Image> = mutableMapOf(),
    ) {
        fun getImageOrCompute(asset: String, width: Int, height: Int): Image? {
            val key = getKey(asset, width, height)
            val current = cache[key]
            if (current == null) {
                val image = getImageResource(asset, width, height) ?: return null
                cache[key] = image
                return image
            }

            return current
        }

        private fun getKey(asset: String, width: Int, height: Int): String =
            "[${width}x$height]$asset"
    }
}

/**
 * Get an [Image] from the jar resources with the [asset] path.
 *
 * If [width] is specified and higher than zero, the image will be
 * resized to that width. The same applies to the [height].
 *
 * If one is specified without the other, the image will be resized
 * maintaining the same aspect ratio.
 */
inline fun getImageResource(asset: String, width: Int, height: Int): Image? {
    val resource: InputStream = getResource(asset) ?: return null
    var bImage: BufferedImage = ImageIO.read(resource)

    if (width > 0 || height > 0) {
        val newWidth = if (width > 0) width else (height.toDouble() / bImage.height * bImage.width).toInt()
        val newHeight = if (height > 0) height else (width.toDouble() / bImage.width * bImage.height).toInt()

        val resized = BufferedImage(newWidth, newHeight, 2)
        val graphics = resized.createGraphics()
        graphics.drawImage(bImage, 0, 0, newWidth, newHeight, null)
        graphics.dispose()
        bImage = resized
    }

    val rgb = bImage.getRGB(0, 0, bImage.width, bImage.height, null, 0, bImage.width)
    val buffer = JetpImageUtil.dither2Minecraft(rgb, bImage.width)

    return ImageAsset(asset, buffer.array(), bImage.width)
}

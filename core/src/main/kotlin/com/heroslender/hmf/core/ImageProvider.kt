package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.components.Image

interface ImageProvider {
    enum class ImageResizeMode {
        COVER,
        CONTAIN,
        STRETCH,
    }

    /**
     * Get an [Image] from the jar resources with the [url] path.
     * If [cached] is true, a cached value of the image will be returned.
     *
     * If [width] is specified and higher than zero, the image will be
     * resized to that width. The same applies to the [height].
     *
     * If one is specified without the other, the image will be resized
     * maintaining the same aspect ratio.
     */
    fun getImage(
        url: String,
        width: Int = -1,
        height: Int = -1,
        resizeMode: ImageResizeMode = ImageResizeMode.STRETCH,
        cached: Boolean = true,
    ): Image?
}
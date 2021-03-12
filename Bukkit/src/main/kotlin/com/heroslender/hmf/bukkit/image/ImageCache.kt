package com.heroslender.hmf.bukkit.image

class ImageCache(
    private val cache: MutableMap<String, ImageAsset> = mutableMapOf(),
) {
    fun getImageOrCompute(asset: String): ImageAsset? {
        val current = cache[asset]
        if (current == null) {
            val image = ImageLoader.getImageResource(asset) ?: return null
            cache[asset] = image
            return image
        }

        return current
    }
}
package com.heroslender.hmf.bukkit.image

import com.heroslender.hmf.core.utils.getResource
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object ImageLoader {

    fun getImageResource(asset: String): ImageAsset? {
        val bImage: BufferedImage = ImageIO.read(getResource(asset) ?: return null)
        val rgb = bImage.getRGB(0, 0, bImage.width, bImage.height, null, 0, bImage.width)
        val buffer = JetpImageUtil.dither2Minecraft(rgb, bImage.width)

        return ImageAsset(asset, buffer.array(), bImage.width)
    }
}
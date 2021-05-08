package com.heroslender.hmf.bukkit.manager

import com.heroslender.hmf.core.ui.components.Image

interface ImageManager {
    fun getImage(asset: String, width: Int, height: Int, cached: Boolean): Image?
}
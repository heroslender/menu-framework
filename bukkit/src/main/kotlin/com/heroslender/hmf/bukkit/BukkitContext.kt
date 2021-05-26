package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.bukkit.modifiers.ClickEventData
import com.heroslender.hmf.core.RenderContext

interface BukkitContext : RenderContext {
    override val canvas: MapCanvas

    fun handleClick(x: Int, y: Int, data: ClickEventData)
}
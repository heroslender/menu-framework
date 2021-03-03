package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.map.MapCanvas

class Context(
    override val canvas: MapCanvas,
) : BukkitContext {
    private var callback: () -> Unit = {}

    override fun update() {
        callback()
    }

    override fun onUpdate(callback: () -> Unit) {
        this.callback = callback
    }
}
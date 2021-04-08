package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent

class Context(
    override val manager: BukkitMenuManager,
    override val canvas: MapCanvas,
    override var root: Composable? = null,
) : BukkitContext {
    private var callback: () -> Unit = {}

    override fun update() {
        callback()
    }

    override fun onUpdate(callback: () -> Unit) {
        this.callback = callback
    }

    override fun handleClick(x: Int, y: Int, type: ClickEvent.Type) {
        root?.foldOut(false) { acc, component ->
            if (!acc) {
                if (component.checkIntersects(x, y)) {
                    component.tryClick(x, y, type)

                    return@foldOut true
                }
            }

            return@foldOut acc
        }
    }
}
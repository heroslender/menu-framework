package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.components.RootComponent
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickListener
import com.heroslender.hmf.core.ui.modifier.modifiers.CursorListener

class Context(
    override val manager: BukkitMenuManager,
    override val canvas: MapCanvas,
    override var root: RootComponent? = null,
) : BukkitContext {
    private var callback: () -> Unit = {}
    private val interactables: MutableList<Interactable> = mutableListOf()

    override fun update() {
        callback()
        computeInteractables()
    }

    override fun onUpdate(callback: () -> Unit) {
        this.callback = callback
    }

    override fun handleClick(x: Int, y: Int, type: ClickEvent.Type) {
        for (i in interactables) {
            if (i.isInside(x, y)) {
                i.clickListener.invoke(ClickEvent(x, y, type, i.component))
                return
            }
        }
    }

    private fun computeInteractables() {
        val components: MutableList<Interactable> = mutableListOf()
        root?.foldOut(components, op = { acc, component ->
            component.asInteractableOrNull()?.also {
                acc.add(it)
            }

            acc
        })

        synchronized(interactables) {
            interactables.clear()
            interactables.addAll(components)
        }
    }

    private fun Component.asInteractableOrNull(): Interactable? {
        val listener = modifier.extra.foldIn<CursorListener>(CursorListener) { acc, element ->
            if (element is CursorListener) {
                return@foldIn element
            }

            return@foldIn acc
        }

        if (listener == CursorListener) {
            return null
        }

        val offsetX = positionX
        val offsetY = positionY
        return Interactable(offsetX, offsetY, offsetX + width, offsetY + height, listener::onClick, this)
    }

    private data class Interactable(
        val minX: Int,
        val minY: Int,
        val maxX: Int,
        val maxY: Int,
        val clickListener: ClickListener,
        val component: Component,
    ) {
        fun isInside(x: Int, y: Int): Boolean {
            return x in minX..maxX && y in minY..maxY
        }
    }
}
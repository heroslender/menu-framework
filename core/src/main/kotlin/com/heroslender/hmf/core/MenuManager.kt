package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.AbstractNode
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.core.ui.components.Image

interface MenuManager<O, M : Menu> {
    /**
     * Get a menu that belongs to [owner].
     */
    fun get(owner: O): M?

    /**
     * Remove a menu bound to [owner] from this manager.
     */
    fun remove(owner: O): M?

    /**
     * Register a [menu] to this manager.
     */
    fun add(menu: M)

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
    fun getImage(url: String, width: Int = -1, height: Int = -1, cached: Boolean = true): Image?

    /**
     * Tick render a menu.
     * If the menu does not have any components flagged as dirty, then
     * it wont re-render.
     */
    fun render(menu: Menu): Boolean {
        val context = menu.context
        val rootComponent: Composable = context.root ?: return false

        val dirtyComponents = rootComponent.foldIn(mutableListOf<Composable>()) { list, component ->
            if (component.isDirty && component is Composable) {
                list.add(component)
            }

            return@foldIn list
        }

        if (dirtyComponents.isEmpty()) {
            return false
        }

        for (component in dirtyComponents) {
            component.compose()

            component.measure((component as AbstractNode).constraints)
        }

        (rootComponent as ComposableNode).outerWrapper.placeAt(0, 0)
        val rendered = rootComponent.draw(context.canvas)
        if (rendered) {
            rootComponent.foldIn(Unit) { _, c ->
                println("${"  ".repeat(c.deepLevel)}> ${c.name} -> ${c.width}x${c.height} at ${c.positionX} ${c.positionY}")
            }
            context.update()
            return true
        }

        return false
    }
}
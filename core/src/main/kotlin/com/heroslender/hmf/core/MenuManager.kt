package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.AbstractNode
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.ComposableNode

interface MenuManager<M : Menu> {

    val imageProvider: ImageProvider

    /**
     * Register the [menu] to this manager.
     */
    fun register(menu: M)

    /**
     * Removes the [menu] from this manager.
     */
    fun unregister(menu: M)

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
            context.update()
            return true
        }

        return false
    }

    /**
     * Disposes this menu manager, unregisters listeners and
     * cancels running tasks.
     */
    fun dispose()
}
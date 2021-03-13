package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.Image
import com.heroslender.hmf.core.ui.components.RootComponent

interface MenuManager<O, M : Menu> {
    fun get(owner: O): M?

    fun remove(owner: O): M?

    fun add(menu: M)

    fun getImage(url: String, cached: Boolean = true): Image?

    fun render(menu: Menu): Boolean {
        val context = menu.context
        val rootComponent: RootComponent = context.root ?: return false

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
        }

        rootComponent.foldIn(Unit) { _, component ->
            // Force everything to re-render, fixes some issues
            // I need to find a better way to fix this
            component.flagDirty()
        }
        rootComponent.reRender(0, 0)

        val rendered = rootComponent.render()
        if (rendered) {
            context.update()
            return true
        }

        return false
    }
}
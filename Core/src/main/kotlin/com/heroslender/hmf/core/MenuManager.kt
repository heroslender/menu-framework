package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.RootComponent

interface MenuManager<O, M : Menu> {
    fun get(owner: O): M?

    fun remove(owner: O): M?

    fun add(menu: M)

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

        rootComponent.reRender(0, 0, context)

        val rendered = rootComponent.render()
        if (rendered) {
            context.update()
            return true
        }

        return false
    }
}
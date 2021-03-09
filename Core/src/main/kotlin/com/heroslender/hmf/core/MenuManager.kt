package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.RootComponent

interface MenuManager<O, M : Menu> {
    fun get(owner: O): M?

    fun remove(owner: O): M?

    fun add(menu: M)

    fun render(menu: Menu): Boolean {
        val context = menu.context
        val rootComponent: RootComponent = context.root ?: return false

        val dirtyComponents = rootComponent.foldIn(mutableListOf<Component>()) { list, component ->
            if (component.isDirty) {
                list.add(component)
            }

            return@foldIn list
        }

        if (dirtyComponents.isEmpty()) {
            return false
        }

        var requireFullRender = false
        for (component in dirtyComponents) {
            if (component !is Composable) {
                continue
            }

            val prevWidth = component.width
            val prevHeight = component.height

            component.compose()
            component.reRender(component.positionX, component.positionY, context)

            if (prevWidth != component.width || prevHeight != component.height) {
                // The component size has changed, re-render the whole thing
                requireFullRender = true
                break
            }
        }

        if (requireFullRender) {
            rootComponent.reRender(0, 0, context)
        }

        val rendered = rootComponent.render()
        if (rendered) {
            context.update()
            return true
        }

        return false
    }
}
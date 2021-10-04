package com.heroslender.hmf.intellij.preview.components

import java.awt.*

class LayeredPaneLayout(private val target: Container) : LayoutManager {
    override fun addLayoutComponent(name: String?, comp: Component?) {}
    override fun layoutContainer(container: Container) {
        for (component in container.components) {
            component.bounds = Rectangle(
                component.x,
                component.y,
                component.preferredSize.width,
                component.preferredSize.height
            )
        }
    }

    override fun minimumLayoutSize(parent: Container?): Dimension {
        return preferredLayoutSize(parent)
    }

    override fun preferredLayoutSize(parent: Container?): Dimension {
        return Dimension(target.width, target.height)
    }

    override fun removeLayoutComponent(comp: Component?) {}
}
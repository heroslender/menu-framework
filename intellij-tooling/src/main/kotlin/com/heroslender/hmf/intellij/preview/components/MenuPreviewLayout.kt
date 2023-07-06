package com.heroslender.hmf.intellij.preview.components

import java.awt.*

class MenuPreviewLayout(private val target: Container) : LayoutManager2 {
    var content: Component? = null
    var toolbar: Component? = null

    override fun addLayoutComponent(comp: Component, constraints: Any) {
        if (constraints is Int) {
            if (constraints == 0) {
                content = comp
            } else if (constraints == 1) {
                toolbar = comp
            }
        }
    }

    override fun maximumLayoutSize(target: Container?): Dimension {
        return preferredLayoutSize(target)
    }

    override fun getLayoutAlignmentX(target: Container?): Float {
        return 1F
    }

    override fun getLayoutAlignmentY(target: Container?): Float {
        return 0F
    }

    override fun invalidateLayout(target: Container?) {
    }

    override fun addLayoutComponent(name: String?, comp: Component?) {}
    override fun layoutContainer(container: Container) {
        content?.also { component ->
            component.bounds = Rectangle(
                component.x,
                component.y,
                this.target.size.width,
                this.target.size.height
            )
        }

        toolbar?.also { component ->
            component.bounds = Rectangle(
                this.target.width - (component.preferredSize.width + 20),
                this.target.height - (component.preferredSize.height + 50),
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
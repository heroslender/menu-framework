package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.node.ComponentWrapper
import com.heroslender.hmf.core.ui.modifier.node.InnerComponentWrapper

class ComposableNode(
    parent: Composable?,
    modifier: Modifier = Modifier,
    renderContext: RenderContext = parent!!.renderContext,
    private val content: Composable.() -> Unit,
) : AbstractNode(parent, modifier, renderContext), Composable {

    override var childOffsetX: Int = 0
        private set
    override var childOffsetY: Int = 0
        private set

    private val _children: MutableList<Component> = mutableListOf()
    override val children: List<Component>
        get() = _children

    override val innerWrapper: ComponentWrapper = InnerComponentWrapper(this)
    override val outerWrapper: ComponentWrapper = defaultOuter(modifier, innerWrapper)

    override fun addChild(child: Component) {
        _children.add(child)
    }

    override fun draw(canvas: Canvas): Boolean {
        var result: Boolean = super.draw(canvas)
        for (child in children) {
            result = result or child.draw(canvas)
        }

        return result
    }

    override fun compose() {
        _children.clear()

        content()

        children.filterIsInstance<Composable>()
            .forEach(Composable::compose)
    }

    /**
     * Calculate the offset added to children, this
     * could be padding for example.
     */
    private inline fun childOffset(metric: Int, op: (wrapper: ComponentWrapper) -> Int): Int {
        var m = metric
        var wrapper: ComponentWrapper? = outerWrapper.wrapped
        while (wrapper != null) {
            m += op(wrapper)

            wrapper = wrapper.wrapped
        }

        return m
    }

    override fun onNodePlaced() {
        childOffsetX = childOffset(0) { it.x }
        childOffsetY = childOffset(0) { it.y }

        super.onNodePlaced()
    }
}
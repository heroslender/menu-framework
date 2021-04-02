package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.node.ComponentWrapper
import com.heroslender.hmf.core.ui_v2.modifier.node.InnerComponentWrapper

class ComposableNode(
    parent: Composable?,
    modifier: Modifier = Modifier,
    renderContext: RenderContext = parent!!.renderContext,
    private val content: Composable.() -> Unit,
) : AbstractNode(parent, modifier, renderContext), Composable {

    override var measurableGroup: MeasurableGroup = MeasurableGroup

    private val _children: MutableList<Component> = mutableListOf()
    override val children: List<Component>
        get() = _children

    override val innerWrapper: ComponentWrapper = InnerComponentWrapper(this)
    override val outerWrapper: ComponentWrapper = defaultOuter(modifier, innerWrapper)

    override fun addChild(child: Component) {
        _children.add(child)
    }

    override fun compose() {
        _children.clear()

        content()

        children.filterIsInstance<Composable>()
            .forEach(Composable::compose)
    }

    override fun render(): Boolean {

        return true
    }

    override fun reRender(offsetX: Int, offsetY: Int) {
        this.positionX = offsetX
        this.positionY = offsetY
    }
}
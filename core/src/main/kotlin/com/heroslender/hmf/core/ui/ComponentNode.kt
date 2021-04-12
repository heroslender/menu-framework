package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.node.ComponentWrapper
import com.heroslender.hmf.core.ui.modifier.node.InnerComponentWrapper

class ComponentNode(
    parent: Composable?,
    modifier: Modifier = Modifier,
    renderContext: RenderContext = parent!!.renderContext,
) : AbstractNode(parent, modifier, renderContext) {
    override val innerWrapper: ComponentWrapper = InnerComponentWrapper(this)
    override val outerWrapper: ComponentWrapper = defaultOuter(modifier, innerWrapper)
}

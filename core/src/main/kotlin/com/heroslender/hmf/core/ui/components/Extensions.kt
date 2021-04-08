package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.ComponentNode
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.core.ui.modifier.Modifier


inline fun Composable.appendComposable(
    modifier: Modifier,
    noinline content: Composable.() -> Unit,
    transformer: Composable.() -> Unit = {},
) {
    val node = ComposableNode(this, modifier, renderContext, content)
    node.transformer()
    addChild(node)
}

inline fun Composable.appendComponent(
    modifier: Modifier,
    transformer: Component.() -> Unit = {},
) {
    val node = ComponentNode(this, modifier, renderContext)
    node.transformer()
    addChild(node)
}

package com.heroslender.hmf.core.ui_v2.components

import com.heroslender.hmf.core.ui_v2.Component
import com.heroslender.hmf.core.ui_v2.ComponentNode
import com.heroslender.hmf.core.ui_v2.Composable
import com.heroslender.hmf.core.ui_v2.ComposableNode
import com.heroslender.hmf.core.ui_v2.modifier.Modifier


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

package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.containers.BoxComponent
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.horizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginVertical
import com.heroslender.hmf.core.ui.modifier.vertical

class RootComponent(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    renderContext: RenderContext,
    content: Composable.() -> Unit,
) : BoxComponent(null, renderContext, modifier, builder = content) {
    @Suppress("SetterBackingFieldAssignment")
    override var width: Int = width
        set(_) {}

    @Suppress("SetterBackingFieldAssignment")
    override var height: Int = height
        set(_) {}

    override val availableWidth: Int
        get() = width - modifier.padding.horizontal - modifier.marginHorizontal
    override val availableHeight: Int
        get() = height - modifier.padding.vertical - modifier.marginVertical
}
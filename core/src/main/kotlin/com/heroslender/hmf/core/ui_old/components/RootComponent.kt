package com.heroslender.hmf.core.ui_old.components

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_old.Composable
import com.heroslender.hmf.core.ui_old.components.containers.BoxComponent
import com.heroslender.hmf.core.ui_old.modifier.Modifier
import com.heroslender.hmf.core.ui_old.modifier.horizontal
import com.heroslender.hmf.core.ui_old.modifier.vertical

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
        get() = width - modifier.padding.horizontal
    override val availableHeight: Int
        get() = height - modifier.padding.vertical
}
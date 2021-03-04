package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.containers.RowComponent
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.marginHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginVertical
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingVertical

class RootComponent(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    content: Composable.() -> Unit,
) : RowComponent(null, modifier, builder = content) {
    @Suppress("SetterBackingFieldAssignment")
    override var width: Int = width
        set(_) {}

    @Suppress("SetterBackingFieldAssignment")
    override var height: Int = height
        set(_) {}

    override val availableWidth: Int
        get() = width - modifier.paddingHorizontal - modifier.marginHorizontal
    override val availableHeight: Int
        get() = height - modifier.paddingVertical - modifier.marginVertical
}
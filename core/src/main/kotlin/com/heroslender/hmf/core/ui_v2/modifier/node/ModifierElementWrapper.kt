package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.ui_v2.modifier.Modifier

abstract class ModifierElementWrapper<T : Modifier.Element>(
    override val wrapped: ComponentWrapper,
    val modifier: T,
) : ComponentWrapper(wrapped.component)
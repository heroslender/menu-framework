package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.ui.modifier.type.CursorClickModifier

class ClickableModifierWrapper(
    wrapped: ComponentWrapper,
    modifier: CursorClickModifier,
) : ModifierElementWrapper<CursorClickModifier>(wrapped, modifier) {

    fun click(x: Int, y: Int, data: Any): Boolean =
        with(modifier) {
            return onClick(
                x = x,
                y = y,
                component = component,
                data = data,
            )
        }
}
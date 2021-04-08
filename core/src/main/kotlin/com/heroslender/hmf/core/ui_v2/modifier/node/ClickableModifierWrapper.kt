package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.ui_v2.modifier.modifiers.ClickEvent
import com.heroslender.hmf.core.ui_v2.modifier.type.CursorClickModifier

class ClickableModifierWrapper(
    wrapped: ComponentWrapper,
    modifier: CursorClickModifier,
) : ModifierElementWrapper<CursorClickModifier>(wrapped, modifier) {

    fun click(x: Int, y: Int, type: ClickEvent.Type) {
        with(modifier) {
            onClick(
                ClickEvent(
                    x = x,
                    y = y,
                    type = type,
                    component = component,
                )
            )
        }
    }
}
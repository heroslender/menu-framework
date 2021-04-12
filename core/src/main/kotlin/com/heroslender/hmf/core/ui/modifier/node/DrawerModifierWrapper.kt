package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.modifier.type.DrawerModifier

class DrawerModifierWrapper(
    wrapped: ComponentWrapper,
    modifier: DrawerModifier,
) : ModifierElementWrapper<DrawerModifier>(wrapped, modifier) {

    override fun draw(canvas: Canvas) {
        withOffset(canvas) {
            with(modifier) {
                onDraw(canvas)
            }
        }

        super.draw(canvas)
    }
}
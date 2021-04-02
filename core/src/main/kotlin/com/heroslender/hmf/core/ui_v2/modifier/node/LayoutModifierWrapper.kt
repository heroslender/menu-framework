package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Placeable
import com.heroslender.hmf.core.ui_v2.LayoutModifier
import com.heroslender.hmf.core.ui_v2.MeasureScope

class LayoutModifierWrapper(
    wrapped: ComponentWrapper,
    modifier: LayoutModifier,
) : ModifierElementWrapper<LayoutModifier>(wrapped, modifier) {

    override fun measure(constraints: Constraints): Placeable {
        with(modifier) {
            measureResult = MeasureScope.measure(wrapped, constraints)
        }

        return this
    }

    override fun placeAt(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}

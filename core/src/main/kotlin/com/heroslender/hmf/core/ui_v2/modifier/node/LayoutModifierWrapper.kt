package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.ui_v2.MeasureScope
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable

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
}

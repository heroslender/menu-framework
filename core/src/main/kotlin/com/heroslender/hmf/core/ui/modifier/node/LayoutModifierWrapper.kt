package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.modifier.Placeable

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

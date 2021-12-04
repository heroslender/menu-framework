package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.Placeable

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

    override fun minIntrinsicWidth(height: Int): Int = with(modifier) {
        MeasureScope.minIntrinsicWidth(wrapped, height)
    }

    override fun maxIntrinsicWidth(height: Int): Int = with(modifier) {
        MeasureScope.maxIntrinsicWidth(wrapped, height)
    }

    override fun minIntrinsicHeight(width: Int): Int = with(modifier) {
        MeasureScope.minIntrinsicHeight(wrapped, width)
    }

    override fun maxIntrinsicHeight(width: Int): Int = with(modifier) {
        MeasureScope.maxIntrinsicHeight(wrapped, width)
    }
}

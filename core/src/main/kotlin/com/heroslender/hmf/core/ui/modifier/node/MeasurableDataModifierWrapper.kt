package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.ui.modifier.type.MeasurableDataModifier

class MeasurableDataModifierWrapper(
    wrapped: ComponentWrapper,
    modifier: MeasurableDataModifier,
) : ModifierElementWrapper<MeasurableDataModifier>(wrapped, modifier) {

    override val parentData: Any?
        get() = modifier.modifyData(wrapped.parentData)

}
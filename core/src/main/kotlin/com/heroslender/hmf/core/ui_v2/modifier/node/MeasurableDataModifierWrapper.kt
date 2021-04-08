package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.ui_v2.modifier.type.MeasurableDataModifier

class MeasurableDataModifierWrapper(
    wrapped: ComponentWrapper,
    modifier: MeasurableDataModifier,
) : ModifierElementWrapper<MeasurableDataModifier>(wrapped, modifier) {

    override val data: Any?
        get() = modifier.modifyData(wrapped.data)

}
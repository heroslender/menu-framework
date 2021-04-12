package com.heroslender.hmf.core.ui.modifier.type

import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier

interface LayoutModifier : Modifier.Element {

    fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureScope.MeasureResult
}

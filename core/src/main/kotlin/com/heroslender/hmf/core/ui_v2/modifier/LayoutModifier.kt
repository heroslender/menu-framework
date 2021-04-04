package com.heroslender.hmf.core.ui_v2.modifier

import com.heroslender.hmf.core.ui_v2.Measurable
import com.heroslender.hmf.core.ui_v2.MeasureScope

interface LayoutModifier : Modifier.Element {

    fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureScope.MeasureResult
}

package com.heroslender.hmf.core.ui_v2.modifier.type

import com.heroslender.hmf.core.ui_v2.Measurable
import com.heroslender.hmf.core.ui_v2.MeasureScope
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier

interface LayoutModifier : Modifier.Element {

    fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureScope.MeasureResult
}

package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasurableGroup
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints

inline fun newMeasurableGroup(
    crossinline op: MeasureScope.(measurables: List<Measurable>, constraints: Constraints) -> MeasureScope.MeasureResult,
): MeasurableGroup = object : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = op(measurables, constraints)
}

package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.ui_v2.Component
import com.heroslender.hmf.core.ui_v2.Composable
import com.heroslender.hmf.core.ui_v2.MeasureScope
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Placeable

class InnerComponentWrapper(component: Component) : ComponentWrapper(component) {

    override fun measure(constraints: Constraints): Placeable {
        if (component is Composable) {
            with(component.measurableGroup) {
                measureResult = MeasureScope.measure(component.children, constraints)
            }
        }

        return this
    }

    override fun placeAt(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}
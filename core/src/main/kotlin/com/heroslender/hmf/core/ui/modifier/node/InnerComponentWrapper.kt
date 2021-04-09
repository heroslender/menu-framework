package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.Placeable

class InnerComponentWrapper(component: Component) : ComponentWrapper(component) {

    override val data: Any?
        get() = null

    override fun measure(constraints: Constraints): Placeable {
        val children = if (component is Composable) component.children else emptyList()

        with(component.measurableGroup) {
            measureResult = MeasureScope.measure(children, constraints)
        }

        return this
    }

    override fun placeAt(x: Int, y: Int) {
        this.x = x
        this.y = y

        component.onNodePlaced()
    }

    override fun draw(canvas: Canvas) {}
}
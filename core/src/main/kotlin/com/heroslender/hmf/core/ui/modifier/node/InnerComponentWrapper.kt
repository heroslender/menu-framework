package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.Constraints

class InnerComponentWrapper(component: Component) : ComponentWrapper(component) {

    override val parentData: Any?
        get() = null

    override fun measure(constraints: Constraints): Placeable {
        with(component.measurableGroup) {
            measureResult = MeasureScope.measure(component.children, constraints)
        }

        return this
    }

    override fun placeAt(x: Int, y: Int) {
        this.x = x
        this.y = y

        component.onNodePlaced()
    }

    override fun draw(canvas: Canvas) {}

    override fun minIntrinsicWidth(height: Int): Int = with(component.measurableGroup) {
        return MeasureScope.minIntrinsicWidth(component.children, height)
    }

    override fun maxIntrinsicWidth(height: Int): Int = with(component.measurableGroup) {
        return MeasureScope.maxIntrinsicWidth(component.children, height)
    }

    override fun minIntrinsicHeight(width: Int): Int = with(component.measurableGroup) {
        return MeasureScope.minIntrinsicHeight(component.children, width)
    }

    override fun maxIntrinsicHeight(width: Int): Int = with(component.measurableGroup) {
        return MeasureScope.maxIntrinsicHeight(component.children, width)
    }
}
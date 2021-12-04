package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.Constraints

class InnerComponentWrapper(component: Component) : ComponentWrapper(component) {

    override val parentData: Any?
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

    override fun minIntrinsicWidth(height: Int): Int {
        val children = if (component is Composable) component.children else emptyList()

        with(component.measurableGroup) {
            return MeasureScope.minIntrinsicWidth(children, height)
        }
    }

    override fun maxIntrinsicWidth(height: Int): Int {
        val children = if (component is Composable) component.children else emptyList()

        with(component.measurableGroup) {
            return MeasureScope.maxIntrinsicWidth(children, height)
        }
    }

    override fun minIntrinsicHeight(width: Int): Int {
        val children = if (component is Composable) component.children else emptyList()

        with(component.measurableGroup) {
            return MeasureScope.minIntrinsicHeight(children, width)
        }
    }

    override fun maxIntrinsicHeight(width: Int): Int {
        val children = if (component is Composable) component.children else emptyList()

        with(component.measurableGroup) {
            return MeasureScope.maxIntrinsicHeight(children, width)
        }
    }
}
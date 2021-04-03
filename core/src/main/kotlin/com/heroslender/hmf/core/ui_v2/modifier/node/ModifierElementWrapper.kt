package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui_v2.MeasureScope
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable

abstract class ModifierElementWrapper<T : Modifier.Element>(
    override val wrapped: ComponentWrapper,
    val modifier: T,
) : ComponentWrapper(wrapped.component) {

    override fun placeAt(x: Int, y: Int) {
        this.x = x
        this.y = y

        measureResult.placeChildren()
    }

    override fun measure(constraints: Constraints): Placeable {
        val placeable = wrapped.measure(constraints)
        measureResult = object : MeasureScope.MeasureResult {
            override val width: Int = wrapped.measureResult.width
            override val height: Int = wrapped.measureResult.height

            override fun placeChildren() {
                placeable.placeAt(0, 0)
            }
        }

        return this
    }

    override fun draw(canvas: Canvas) {
        withOffset(canvas) {
            wrapped.draw(canvas)
        }
    }
}
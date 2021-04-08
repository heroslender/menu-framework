package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.Placeable

abstract class ModifierElementWrapper<T : Modifier.Element>(
    override val wrapped: ComponentWrapper,
    val modifier: T,
) : ComponentWrapper(wrapped.component) {

    override val data: Any?
        get() = wrapped.data

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
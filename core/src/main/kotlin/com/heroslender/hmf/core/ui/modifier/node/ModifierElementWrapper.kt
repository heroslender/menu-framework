package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier

abstract class ModifierElementWrapper<T : Modifier.Element>(
    override val wrapped: ComponentWrapper,
    val modifier: T,
) : ComponentWrapper(wrapped.component) {

    override val parentData: Any?
        get() = wrapped.parentData

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
        if (!isVisible) {
            return
        }

        withOffset(canvas) {
            wrapped.draw(canvas)
        }
    }

    override fun minIntrinsicWidth(height: Int): Int = wrapped.minIntrinsicWidth(height)

    override fun maxIntrinsicWidth(height: Int): Int = wrapped.maxIntrinsicWidth(height)

    override fun minIntrinsicHeight(width: Int): Int = wrapped.minIntrinsicHeight(width)

    override fun maxIntrinsicHeight(width: Int): Int = wrapped.maxIntrinsicHeight(width)
}
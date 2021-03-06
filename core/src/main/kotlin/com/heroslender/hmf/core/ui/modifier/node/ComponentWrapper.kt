package com.heroslender.hmf.core.ui.modifier.node

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Measurable
import com.heroslender.hmf.core.ui.MeasureScope
import com.heroslender.hmf.core.ui.AbstractPlaceable

abstract class ComponentWrapper(open val component: Component) : AbstractPlaceable(), Measurable {
    open val wrapped: ComponentWrapper? = null

    /**
     * Whether this is the outer component in the chain.
     */
    var isOuter: Boolean = false

    private var _measureResult: MeasureScope.MeasureResult? = null
    var measureResult: MeasureScope.MeasureResult
        get() = _measureResult ?: error("Unmeasured wrapper")
        protected set(value) {
            if (value == _measureResult) {
                return
            }

            _measureResult = value

            width = value.width
            height = value.height
        }

    var x: Int = 0
    var y: Int = 0

    abstract fun draw(canvas: Canvas)

    inline fun withOffset(canvas: Canvas, op: () -> Unit) {
        if (isOuter) {
            op()
            return
        }

        val x = this.x
        val y = this.y

        canvas.addOffset(x, y)
        op()
        canvas.addOffset(-x, -y)
    }
}
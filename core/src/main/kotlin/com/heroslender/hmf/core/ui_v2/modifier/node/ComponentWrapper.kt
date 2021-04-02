package com.heroslender.hmf.core.ui_v2.modifier.node

import com.heroslender.hmf.core.ui_v2.Component
import com.heroslender.hmf.core.ui_v2.modifier.AbstractPlaceable
import com.heroslender.hmf.core.ui_v2.Measurable
import com.heroslender.hmf.core.ui_v2.MeasureScope


abstract class ComponentWrapper(open val component: Component) : AbstractPlaceable(), Measurable {
    open val wrapped: ComponentWrapper? = null

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
//            println("${this.component.javaClass.simpleName}: $width $height")
        }

    var x: Int = 0
    var y: Int = 0

}
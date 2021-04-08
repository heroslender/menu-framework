package com.heroslender.hmf.core.ui_v2.modifier.modifiers

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.type.drawer

fun Modifier.background(color: IColor): Modifier = this then drawer { canvas ->
    for (x in 0 until width) {
        for (y in 0 until height) {
            canvas.setPixel(x, y, color)
        }
    }
}
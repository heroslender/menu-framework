package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.drawer

fun Modifier.background(color: IColor): Modifier = this then drawer { canvas ->
    for (x in 0 until width) {
        for (y in 0 until height) {
            canvas.setPixel(x, y, color)
        }
    }
}
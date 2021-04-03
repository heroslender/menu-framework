package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable

interface DrawerModifier : Modifier.Element {

    fun Placeable.onDraw(canvas: Canvas)
}

fun drawer(op: Placeable.(Canvas) -> Unit): DrawerModifier = object : DrawerModifier {
    override fun Placeable.onDraw(canvas: Canvas) {
        op(canvas)
    }
}

fun Modifier.background(color: IColor): Modifier = this then drawer { canvas ->
    for (x in 0 until width) {
        for (y in 0 until height) {
            canvas.setPixel(x, y, color)
        }
    }
}
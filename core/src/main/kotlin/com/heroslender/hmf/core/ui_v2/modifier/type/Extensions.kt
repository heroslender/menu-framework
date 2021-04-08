package com.heroslender.hmf.core.ui_v2.modifier.type

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui_v2.modifier.Placeable

inline fun drawer(crossinline op: Placeable.(Canvas) -> Unit): DrawerModifier = object : DrawerModifier {
    override fun Placeable.onDraw(canvas: Canvas) {
        op(canvas)
    }
}
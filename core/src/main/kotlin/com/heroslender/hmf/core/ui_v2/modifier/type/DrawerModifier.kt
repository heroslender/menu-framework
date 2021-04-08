package com.heroslender.hmf.core.ui_v2.modifier.type

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable

interface DrawerModifier : Modifier.Element {

    fun Placeable.onDraw(canvas: Canvas)
}

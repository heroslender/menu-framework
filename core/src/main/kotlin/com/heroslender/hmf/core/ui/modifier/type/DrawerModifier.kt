package com.heroslender.hmf.core.ui.modifier.type

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.Placeable

interface DrawerModifier : Modifier.Element {

    fun Placeable.onDraw(canvas: Canvas)
}

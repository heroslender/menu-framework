package com.heroslender.hmf.core.ui_old.modifier.drawer

import com.heroslender.hmf.core.ui_old.Component
import com.heroslender.hmf.core.ui_old.DrawFunc
import com.heroslender.hmf.core.ui_old.modifier.Modifier
import com.heroslender.hmf.core.ui_old.modifier.ModifierExtra
import com.heroslender.hmf.core.ui_old.modifier.then

interface Drawer : ModifierExtra.Element {
    fun Component.onDraw(setPixel: DrawFunc)

    companion object : Drawer {
        override fun Component.onDraw(setPixel: DrawFunc) {}
    }
}

fun drawer(op: Component.(setPixel: DrawFunc) -> Unit): Drawer = object : Drawer {
    override fun Component.onDraw(setPixel: DrawFunc) {
        this.op(setPixel)
    }
}

fun Modifier.combineDrawer(op: Component.(setPixel: DrawFunc) -> Unit): Modifier = this then drawer(op)

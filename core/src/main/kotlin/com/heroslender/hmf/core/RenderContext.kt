package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.components.RootComponent
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent
import com.heroslender.hmf.core.ui_v2.Component

interface RenderContext {
    val manager: MenuManager<out Any, out Menu>
    val canvas: Canvas
    var root: RootComponent?
    var rootNew: Component?

    fun update()

    fun onUpdate(callback: () -> Unit)

    fun handleClick(x: Int, y: Int, type: ClickEvent.Type)
}
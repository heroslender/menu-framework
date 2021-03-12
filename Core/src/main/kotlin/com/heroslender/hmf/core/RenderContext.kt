package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.components.RootComponent
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent

interface RenderContext {
    val manager: MenuManager<out Any, out Menu>
    val canvas: Canvas
    var root: RootComponent?

    fun update()

    fun onUpdate(callback: () -> Unit)

    fun handleClick(x: Int, y: Int, type: ClickEvent.Type)
}
package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent

interface RenderContext {
    val manager: MenuManager<out Menu>
    val canvas: Canvas
    var root: Composable?

    fun update()

    fun onUpdate(callback: () -> Unit)

    fun handleClick(x: Int, y: Int, type: ClickEvent.Type)
}

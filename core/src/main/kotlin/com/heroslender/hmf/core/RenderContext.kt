package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Composable

interface RenderContext {
    val manager: MenuManager<out Menu>
    val canvas: Canvas
    var root: Composable?
    val menu: Menu

    fun update()

    fun onUpdate(callback: () -> Unit)
}

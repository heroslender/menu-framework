package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.RootComponent

interface Menu {
    val context: RenderContext

    fun Composable.getUi()

    fun render() {
        val root = RootComponent(context.canvas.width, context.canvas.height, renderContext = context) {
            getUi()
        }

        root.compose()
        root.reRender(0, 0)

        root.render()

        context.root = root
        context.update()
    }
}
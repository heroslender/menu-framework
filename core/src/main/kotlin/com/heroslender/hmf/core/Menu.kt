package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.maxSize

interface Menu {
    val context: RenderContext

    fun Composable.getUi()

    fun render() {
        val root = ComposableNode(
            parent = null,
            modifier = Modifier.maxSize(context.canvas.width, context.canvas.height),
            renderContext = context
        ) {
            getUi()
        }
        context.root = root

        root.compose()
        root.measure(Constraints())

        root.outerWrapper.placeAt(0, 0)
        root.foldIn(Unit) { _, c ->
            println("${"  ".repeat(c.deepLevel)}> ${c.name} -> ${c.width}x${c.height} at ${c.positionX} ${c.positionY}")
        }
        root.draw(context.canvas)

        context.update()
    }
}
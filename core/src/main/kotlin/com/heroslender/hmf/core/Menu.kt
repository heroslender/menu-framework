package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui_v2.ComposableNode
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.modifiers.maxSize

interface Menu {
    val context: RenderContext

    fun Composable.getUi()
    fun com.heroslender.hmf.core.ui_v2.Composable.getUi2()

    fun render() {
        val root = ComposableNode(
            parent = null,
            modifier = Modifier.maxSize(context.canvas.width, context.canvas.height),
            renderContext = context
        ) {
            getUi2()
        }
        context.rootNew = root

        root.compose()
        root.measure(Constraints())

        root.outerWrapper.placeAt(0, 0)
        root.foldIn(Unit) { _, c ->
            println("${"  ".repeat(c.deepLevel)}> ${c.name} -> ${c.width}x${c.height} at ${c.positionX} ${c.positionY}")
        }
        root.draw(context.canvas)

//        val root2 = RootComponent(context.canvas.width, context.canvas.height, renderContext = context) {}
//        root2.compose()
//        root2.reRender(0, 0)
//        root2.render()
//        context.root = root2

        context.update()
    }
}
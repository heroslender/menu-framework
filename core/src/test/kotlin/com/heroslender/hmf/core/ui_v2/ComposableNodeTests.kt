package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.Menu
import com.heroslender.hmf.core.MenuManager
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.components.RootComponent
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.modifiers.fixedSize
import com.heroslender.hmf.core.ui_v2.modifier.modifiers.padding
import org.junit.jupiter.api.Test

class ComposableNodeTests {

    @Test
    fun sizeTest() {
        val root = ComposableNode(
            parent = null,
            modifier = Modifier.fixedSize(200, 100),
            renderContext = object : RenderContext {
                override val manager: MenuManager<out Any, out Menu>
                    get() = TODO("Not yet implemented")
                override val canvas: Canvas
                    get() = TODO("Not yet implemented")
                override var root: RootComponent?
                    get() = TODO("Not yet implemented")
                    set(value) {}

                override fun update() {
                    TODO("Not yet implemented")
                }

                override fun onUpdate(callback: () -> Unit) {
                    TODO("Not yet implemented")
                }

                override fun handleClick(x: Int, y: Int, type: ClickEvent.Type) {
                    TODO("Not yet implemented")
                }
            }
        ) {
            val child = ComponentNode(this, modifier = Modifier.padding(20))

            val child2 = ComposableNode(this, modifier = Modifier.padding(5)) {
                val child1 = ComponentNode(this, modifier = Modifier.padding(0))
                val child3 = ComponentNode(this)
                addChild(child1)
                addChild(child3)
                addChild(child3)
            }


            addChild(child)
            addChild(child2)
        }

        root.compose()
        root.measure(Constraints())

        root.foldIn(Unit) { _, c ->
            println("${c.javaClass.simpleName} -> ${c.width}x${c.height}")
        }
    }
}
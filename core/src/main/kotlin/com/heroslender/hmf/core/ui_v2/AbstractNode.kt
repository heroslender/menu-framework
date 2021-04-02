package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable
import com.heroslender.hmf.core.ui_v2.modifier.node.ComponentWrapper
import com.heroslender.hmf.core.ui_v2.modifier.node.LayoutModifierWrapper

abstract class AbstractNode(
    override val parent: Composable?,
    override val modifier: Modifier = Modifier,
    override val renderContext: RenderContext = parent!!.renderContext,
) : Component {
    override var positionX: Int = 0
    override var positionY: Int = 0

    override val width: Int
        get() = outerWrapper.width
    override val height: Int
        get() = outerWrapper.height

    abstract val innerWrapper: ComponentWrapper
    abstract val outerWrapper: ComponentWrapper

    override fun measure(constraints: Constraints): Placeable {
        return outerWrapper.measure(constraints)
    }

    internal fun defaultOuter(modifier: Modifier, inner: ComponentWrapper): ComponentWrapper {
        return modifier.foldOut(inner) { mod, prevWrapper ->
            var wrapper = prevWrapper

            if (mod is LayoutModifier) {
                wrapper = LayoutModifierWrapper(wrapper, mod)
            }

            return@foldOut wrapper
        }
    }
}
package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_v2.modifier.*
import com.heroslender.hmf.core.ui_v2.modifier.node.ComponentWrapper
import com.heroslender.hmf.core.ui_v2.modifier.node.DrawerModifierWrapper
import com.heroslender.hmf.core.ui_v2.modifier.node.LayoutModifierWrapper
import com.heroslender.hmf.core.ui_v2.modifier.node.MeasurableDataModifierWrapper

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

    override val data: Any?
        get() = outerWrapper.data

    override var measurableGroup: MeasurableGroup = MeasurableGroup

    override fun measure(constraints: Constraints): Placeable {
        return outerWrapper.measure(constraints)
    }

    override fun onNodePlaced() {
        this.positionX = (parent?.positionX ?: 0) + (parent?.childOffsetX ?: 0) + outerWrapper.x
        this.positionY = (parent?.positionY ?: 0) + (parent?.childOffsetY ?: 0) + outerWrapper.y

        innerWrapper.measureResult.placeChildren()
    }

    internal fun defaultOuter(modifier: Modifier, inner: ComponentWrapper): ComponentWrapper {
        return (modifier.foldOut(inner) { mod, prevWrapper ->
            var wrapper = prevWrapper

            if (mod is DrawerModifier) {
                wrapper = DrawerModifierWrapper(wrapper, mod)
            }
            if (mod is MeasurableDataModifier) {
                wrapper = MeasurableDataModifierWrapper(wrapper, mod)
            }
            if (mod is LayoutModifier) {
                wrapper = LayoutModifierWrapper(wrapper, mod)
            }

            return@foldOut wrapper
        }).also { it.isOuter = true }
    }

    override var isDirty: Boolean = true

    // Keep track of the previous canvas, so that when the state updates
    // this component won't be drawn over the previous
    private var prevCanvas: Canvas? = null

    override fun draw(canvas: Canvas): Boolean {
        if (!isDirty) {
            prevCanvas?.also {
                canvas.draw(it, positionX, positionY)
            }

            return false
        }

        isDirty = false

        // Temporary canvas to handle transparent pixels
        val tempCanvas: Canvas = getPrevCanvas()
        outerWrapper.draw(tempCanvas)

        canvas.draw(tempCanvas, positionX, positionY)

        return true
    }

    private fun getPrevCanvas(): Canvas {
        var prev = this.prevCanvas
        if (prev == null) {
            prev = renderContext.canvas.newCanvas(this.width, this.height)
            this.prevCanvas = prev
        } else if (prev.width != this.width || prev.height != this.height) {
            prev = renderContext.canvas.newCanvas(this.width, this.height)
            this.prevCanvas = prev
        }

        return prev
    }
}
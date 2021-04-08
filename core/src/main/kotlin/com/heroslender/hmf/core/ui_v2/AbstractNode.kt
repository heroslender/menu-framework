package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_v2.modifier.*
import com.heroslender.hmf.core.ui_v2.modifier.modifiers.ClickEvent
import com.heroslender.hmf.core.ui_v2.modifier.node.*
import com.heroslender.hmf.core.ui_v2.modifier.type.CursorClickModifier
import com.heroslender.hmf.core.ui_v2.modifier.type.DrawerModifier
import com.heroslender.hmf.core.ui_v2.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui_v2.modifier.type.MeasurableDataModifier

abstract class AbstractNode(
    override val parent: Composable?,
    override val modifier: Modifier = Modifier,
    override val renderContext: RenderContext = parent!!.renderContext,
) : Component {

    override val name: String = Throwable().stackTrace[2].methodName
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

    override fun checkIntersects(x: Int, y: Int): Boolean {
        val posX = positionX
        val posY = positionY

        return posX < x && x < posX + width
            && posY < y && y < posY + height
    }

    private var hasClickable: Boolean = false

    override fun tryClick(x: Int, y: Int, type: ClickEvent.Type): Boolean {
        if (!hasClickable) {
            return parent?.tryClick(x, y, type) ?: false
        }

        var wrapper: ComponentWrapper? = outerWrapper
        var result = false
        var posX = positionX
        var posY = positionY
        while (wrapper != null) {
            if (!wrapper.isOuter) {
                posX += wrapper.x
                posY += wrapper.y
            }

            if (posX < x && x < posX + width
                && posY < y && y < posY + height
                && wrapper is ClickableModifierWrapper
            ) {
                wrapper.click(x, y, type)

                result = true
            }

            wrapper = wrapper.wrapped
        }

        return result || parent?.tryClick(x, y, type) ?: false
    }

    override fun onNodePlaced() {
        this.positionX = (parent?.positionX ?: 0) + (parent?.childOffsetX ?: 0) + outerWrapper.x
        this.positionY = (parent?.positionY ?: 0) + (parent?.childOffsetY ?: 0) + outerWrapper.y

        innerWrapper.measureResult.placeChildren()
    }

    internal fun defaultOuter(modifier: Modifier, inner: ComponentWrapper): ComponentWrapper {
        return modifier.foldOut(inner) { mod, prevWrapper ->
            var wrapper = prevWrapper

            if (mod is MeasurableDataModifier) {
                wrapper = MeasurableDataModifierWrapper(wrapper, mod)
            }
            if (mod is LayoutModifier) {
                wrapper = LayoutModifierWrapper(wrapper, mod)
            }
            if (mod is DrawerModifier) {
                wrapper = DrawerModifierWrapper(wrapper, mod)
            }
            if (mod is CursorClickModifier) {
                wrapper = ClickableModifierWrapper(wrapper, mod)
                this.hasClickable = true
            }

            return@foldOut wrapper
        }.also { it.isOuter = true }
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
package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_v2.modifier.Constraints
import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable
import com.heroslender.hmf.core.ui_v2.modifier.node.ComponentWrapper
import com.heroslender.hmf.core.ui_v2.modifier.node.DrawerModifierWrapper
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

    override var measurableGroup: MeasurableGroup = MeasurableGroup

    override fun measure(constraints: Constraints): Placeable {
        return outerWrapper.measure(constraints)
    }

    override fun onNodePlaced() {
        this.positionX = (parent?.positionX ?: 0) + (parent?.childOffsetX ?: 0) + outerWrapper.x
        this.positionY = (parent?.positionY ?: 0) + (parent?.childOffsetY ?: 0) + outerWrapper.x

        innerWrapper.measureResult.placeChildren()
    }

    internal fun defaultOuter(modifier: Modifier, inner: ComponentWrapper): ComponentWrapper {
        return modifier.foldOut(inner) { mod, prevWrapper ->
            var wrapper = prevWrapper

            if (mod is DrawerModifier) {
                wrapper = DrawerModifierWrapper(wrapper, mod)
            }
            if (mod is LayoutModifier) {
                wrapper = LayoutModifierWrapper(wrapper, mod)
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
                renderContext.canvas.draw(it, positionX, positionY)
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
        var prev = prevCanvas
        if (prev == null) {
            prev = renderContext.canvas.newCanvas(this.width, this.height)
            this.prevCanvas = prev
        }

        ensureCachedCanvasSize()

        return prev
    }

    private fun ensureCachedCanvasSize() {
        val prevCanvas = this.prevCanvas
        if (prevCanvas != null && (prevCanvas.width != this.width || prevCanvas.height != this.height)) {
            // Component resized, adapt the previous canvas cache to the new component size
            val contextCanvas = renderContext.canvas
            val newPrev = prevCanvas.subCanvas(this.width, this.height)

            if (prevCanvas.width < this.width) {
                for (x in prevCanvas.width until this.width) {
                    for (y in 0 until prevCanvas.height) {
                        newPrev.setPixelByte(x, y, contextCanvas.getPixelByte(x + positionX, y + positionY))
                    }
                }
            }
            if (prevCanvas.height < this.height) {
                for (x in 0 until prevCanvas.width) {
                    for (y in prevCanvas.height until this.height) {
                        newPrev.setPixelByte(x, y, contextCanvas.getPixelByte(x + positionX, y + positionY))
                    }
                }
            }
            if (prevCanvas.width < this.width && prevCanvas.height < this.height) {
                for (x in prevCanvas.width until this.width) {
                    for (y in prevCanvas.height until this.height) {
                        newPrev.setPixelByte(x, y, contextCanvas.getPixelByte(x + positionX, y + positionY))
                    }
                }
            }

            if (prevCanvas.width > this.width) {
                for (x in this.width until prevCanvas.width) {
                    for (y in 0 until prevCanvas.height) {
                        contextCanvas.setPixelByte(x + positionX, y + positionY, prevCanvas.getPixelByte(x, y))
                    }
                }
            }
            if (prevCanvas.height > this.height) {
                for (x in 0 until prevCanvas.width) {
                    for (y in this.height until prevCanvas.height) {
                        contextCanvas.setPixelByte(x + positionX, y + positionY, prevCanvas.getPixelByte(x, y))
                    }
                }
            }
            if (prevCanvas.width > this.width && prevCanvas.height > this.height) {
                for (x in this.width until prevCanvas.width) {
                    for (y in this.height until prevCanvas.height) {
                        contextCanvas.setPixelByte(x + positionX, y + positionY, prevCanvas.getPixelByte(x, y))
                    }
                }
            }

            this.prevCanvas = newPrev
        }
    }
}
package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.drawer.Drawer

/**
 * A [Component] that can be drawn into the canvas.
 */
abstract class DrawableComponent(
    override val parent: Composable?,
    override val modifier: Modifier = Modifier,
    override val renderContext: RenderContext = parent!!.renderContext,
) : Component {
    override var positionX: Int = 0
    override var positionY: Int = 0

    override var isDirty: Boolean = true

    // Keep track of the previous canvas, so that when the state updates
    // this component won't be drawn over the previous
    private var prevCanvas: Canvas? = null

    override var isDisposed: Boolean = false

    override var width: Int = 0
    override var height: Int = 0

    override fun draw(canvas: Canvas) {
        draw(canvas::setPixel)
    }

    override fun draw(setPixel: DrawFunc) {}

    override fun render(): Boolean {
        if (!isDirty) {
            prevCanvas?.also {
                renderContext.canvas.draw(it, positionX, positionY)
            }

            return false
        }

        val context = renderContext
        isDirty = false
        val positionX = positionX
        val positionY = positionY

        // Temporary canvas to handle transparent pixels
        val tempCanvas: Canvas = getPrevCanvas()

        modifier.extra.foldIn(Unit) { acc, elem ->
            if (elem is Drawer) {
                with(elem) {
                    onDraw(tempCanvas::setPixel)
                }
            }

            return@foldIn acc
        }

        draw(tempCanvas)

        context.canvas.draw(tempCanvas, positionX, positionY)

        return true
    }

    override fun reRender(offsetX: Int, offsetY: Int) {
        this.positionX = offsetX
        this.positionY = offsetY
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
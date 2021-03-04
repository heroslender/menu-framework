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
) : Component {
    override var renderContext: RenderContext? = null
    override var positionX: Int = 0
    override var positionY: Int = 0

    override var isDirty: Boolean = true

    override var isDisposed: Boolean = false

    override var width: Int = 0
    override var height: Int = 0

    override fun draw(setPixel: DrawFunc) {
        if (!modifier.extra.any { it is Drawer }) {
            return
        }

        modifier.extra.foldIn(Unit) { acc, elem ->
            if (elem is Drawer) {
                with(elem) {
                    onDraw { x, y, color ->
                        setPixel(x, y, color)
                    }
                }
            }

            return@foldIn acc
        }
    }

    override fun render(): Boolean {
        if (!isDirty) {
            return false
        }

        println("rendering")
        val context = renderContext ?: return false
        isDirty = false
        val positionX = positionX
        val positionY = positionY

        // Temporary canvas to handle transparent pixels
        val tempCanvas: Canvas = context.canvas.newCanvas()

        draw { x, y, color ->
            tempCanvas.setPixel(x + positionX, y + positionY, color)
        }

        context.canvas.draw(tempCanvas)

        return true
    }

    override fun reRender(offsetX: Int, offsetY: Int, context: RenderContext) {
        this.positionX = offsetX
        this.positionY = offsetY
        this.renderContext = context
    }
}

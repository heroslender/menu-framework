package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.Menu
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.node.*
import com.heroslender.hmf.core.ui.modifier.type.CursorClickModifier
import com.heroslender.hmf.core.ui.modifier.type.DrawerModifier
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier
import com.heroslender.hmf.core.ui.modifier.type.MeasurableDataModifier

class LayoutNode : Component {
    override var parent: Component? = null
    override var modifier: Modifier = Modifier
        set(value) {
            field = value
            outerWrapper = defaultOuter(value, innerWrapper)
        }
    override lateinit var menu: Menu
    override var canvas: Canvas? = null

    override var name: String = Throwable().stackTrace[2].methodName
    override var positionX: Int = 0
    override var positionY: Int = 0

    override val width: Int
        get() = outerWrapper.width
    override val height: Int
        get() = outerWrapper.height

    override var childOffsetX: Int = 0
        private set
    override var childOffsetY: Int = 0
        private set

    override val children: MutableList<Component> = mutableListOf()

    val innerWrapper: ComponentWrapper = InnerComponentWrapper(this)
    var outerWrapper: ComponentWrapper = defaultOuter(modifier, innerWrapper)

    override val parentData: Any?
        get() = outerWrapper.parentData

    override var measurableGroup: MeasurableGroup = MeasurableGroup
    var constraints: Constraints = Constraints.Default

    override fun measure(constraints: Constraints): Placeable {
        this.constraints = constraints
        return outerWrapper.measure(constraints)
    }

    override fun checkIntersects(x: Int, y: Int): Boolean {
        val posX = positionX
        val posY = positionY

        return posX < x && x < posX + width
            && posY < y && y < posY + height
    }

    private var hasClickable: Boolean = false

    override fun <T> tryClick(x: Int, y: Int, data: T): Boolean {
        if (!hasClickable) {
            return false
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

            if (posX < x && x < posX + wrapper.width
                && posY < y && y < posY + wrapper.height
                && wrapper is ClickableModifierWrapper
            ) {
                result = result || wrapper.click(x, y, data)
            }

            wrapper = wrapper.wrapped
        }

        return result || parent?.tryClick(x, y, data) ?: false
    }

    override fun onNodePlaced() {
        childOffsetX = childOffset(0) { it.x }
        childOffsetY = childOffset(0) { it.y }

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

    override fun draw(canvas: Canvas?): Boolean {
        try {
            val canvas = canvas ?: this.canvas
            if (width == 0 || height == 0 || !outerWrapper.isVisible || canvas == null) {
                return false
            }

            if (!isDirty) {
                prevCanvas?.also {
                    canvas.draw(it, positionX, positionY)
                }

                var result = false
                for (child in children) {
                    result = result or child.draw(canvas)
                }

                return result
            }

            // Temporary canvas to handle transparent pixels
            val tempCanvas: Canvas = newPrevCanvas(canvas)
            outerWrapper.draw(tempCanvas)

            canvas.draw(tempCanvas, positionX, positionY)

            for (child in children) {
                child.draw(canvas)
            }

            return true
        } finally {
            isDirty = false
        }
    }

    private fun newPrevCanvas(canvas: Canvas): Canvas {
        val prev = canvas.newCanvas(this.width, this.height)
        this.prevCanvas = prev

        return prev
    }

    override fun minIntrinsicWidth(height: Int): Int = outerWrapper.minIntrinsicWidth(height)

    override fun maxIntrinsicWidth(height: Int): Int = outerWrapper.maxIntrinsicWidth(height)

    override fun minIntrinsicHeight(width: Int): Int = outerWrapper.minIntrinsicHeight(width)

    override fun maxIntrinsicHeight(width: Int): Int = outerWrapper.maxIntrinsicHeight(width)

    /**
     * Calculate the offset added to children, this
     * could be padding for example.
     */
    private inline fun childOffset(metric: Int, op: (wrapper: ComponentWrapper) -> Int): Int {
        var m = metric
        var wrapper: ComponentWrapper? = outerWrapper.wrapped
        while (wrapper != null) {
            m += op(wrapper)

            wrapper = wrapper.wrapped
        }

        return m
    }

    override fun toString(): String {
        return "LayoutNode(name='$name')"
    }

    fun dump() {
        if (children.isEmpty()) {
            println(deepSpaces + getDumpText() + ";")
        } else {
            println(deepSpaces + getDumpText() + " {")
            for (child in children) {
                (child as LayoutNode).dump()
            }
            println(deepSpaces + "}")
        }
    }

    fun getDumpText(): String = name + "(" + width + "x" + height + ")"

    val deepSpaces: String
        get() = " ".repeat(deepLevel)
}
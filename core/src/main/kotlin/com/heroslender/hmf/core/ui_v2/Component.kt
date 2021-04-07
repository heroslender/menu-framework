package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_v2.modifier.Modifier

/**
 * A component is a node in the component tree.
 */
interface Component : Measurable {

    val name: String

    /**
     * Component width, including padding and excluding its margin.
     */
    val width: Int

    /**
     * Component height, including padding and excluding its margin.
     */
    val height: Int

    /**
     * Modifiers to be applied to this component.
     */
    val modifier: Modifier
        get() = Modifier

    /**
     * The parent component that holds this.
     *
     * This will be null for the root component.
     */
    val parent: Composable?

    /**
     * The context in which this component is being rendered.
     */
    val renderContext: RenderContext

    /**
     * The `x` position for this component in the canvas.
     */
    var positionX: Int

    /**
     * The `y` position for this component in the canvas.
     */
    var positionY: Int

    /**
     * Whether this component needs to be redrawn.
     */
    var isDirty: Boolean

    /**
     * Flag the component as dirty, this will mark it to
     * be redrawn during the next cycle.
     */
    fun flagDirty() {
        isDirty = true
    }

    var measurableGroup: MeasurableGroup

    fun onNodePlaced()

    fun draw(canvas: Canvas): Boolean

    fun <R> foldIn(acc: R, op: (R, Component) -> R): R = op(acc, this)

    fun <R> foldOut(acc: R, op: (R, Component) -> R): R = op(acc, this)

    /**
     * Used for debugging.
     * Returns how deep this component is in the tree
     */
    val deepLevel: Int
        get() {
            var i = 0
            var c = parent
            while (c != null) {
                i++

                c = c.parent
            }

            return i
        }
}
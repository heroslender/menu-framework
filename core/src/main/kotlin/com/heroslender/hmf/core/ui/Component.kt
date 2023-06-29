package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.modifier.Modifier

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
    var modifier: Modifier

    var canvas: Canvas?

    /**
     * The parent component that holds this.
     *
     * This will be null for the root component.
     */
    var parent: Component?

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

    val childOffsetX: Int
    val childOffsetY: Int

    /**
     * List of children this composable holds.
     */
    val children: MutableList<Component>

    /**
     * Flag the component as dirty, this will mark it to
     * be redrawn during the next cycle.
     */
    fun flagDirty() {
        isDirty = true
    }

    fun checkIntersects(x: Int, y: Int): Boolean

    fun <T>tryClick(x: Int, y: Int, data: T): Boolean

    var measurableGroup: MeasurableGroup

    fun onNodePlaced()

    fun draw(canvas: Canvas?): Boolean

    fun <R> foldIn(acc: R, op: (R, Component) -> R): R {
        var a = op(acc, this)

        for (child in children) {
            a = child.foldIn(a, op)
        }

        return a
    }

    fun <R> foldOut(acc: R, op: (R, Component) -> R): R {
        var a = acc

        for (child in children.reversed()) {
            a = child.foldOut(a, op)
        }

        return op(acc, this)
    }

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
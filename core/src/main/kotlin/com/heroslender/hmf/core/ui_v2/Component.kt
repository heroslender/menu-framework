package com.heroslender.hmf.core.ui_v2

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui_v2.modifier.Modifier

/**
 * A component is a node in the component tree.
 */
interface Component : Measurable {
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
     * Initialize the component position, preparing
     * it for rendering.
     */
    fun reRender(offsetX: Int, offsetY: Int)

    /**
     * Render the component to the canvas if needed
     */
    fun render(): Boolean

    fun <R> foldIn(acc: R, op: (R, Component) -> R): R = op(acc, this)

    fun <R> foldOut(acc: R, op: (R, Component) -> R): R = op(acc, this)
}
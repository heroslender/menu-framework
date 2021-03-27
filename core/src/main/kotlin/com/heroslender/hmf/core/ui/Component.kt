package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.horizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginVertical
import com.heroslender.hmf.core.ui.modifier.vertical

/**
 * A component is a node in the component tree.
 */
interface Component {
    /**
     * Component width, including padding and excluding its margin.
     */
    var width: Int

    /**
     * Component height, including padding and excluding its margin.
     */
    var height: Int

    /**
     * Actual content width, this may not be related to [width].
     */
    val contentWidth: Int
        get() = 0

    /**
     * Actual content height, this may not be related to [height].
     */
    val contentHeight: Int
        get() = 0

    /**
     * The available width this component has to use, based on the parent.
     *
     * Be aware that this space is shared among siblings.
     */
    val availableWidth: Int
        get() = parent!!.width - parent!!.modifier.padding.horizontal - modifier.marginHorizontal

    /**
     * The available height this component has to use, based on the parent.
     *
     * Be aware that this space is shared among siblings.
     */
    val availableHeight: Int
        get() = parent!!.height - parent!!.modifier.padding.vertical - modifier.marginVertical

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
     * Whether this component has been disposed or not.
     */
    var isDisposed: Boolean

    /**
     * Discard this component.
     */
    fun dispose() {
        isDisposed = true
    }

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

    /**
     * Whether this component has state bounded to it
     */
    var hasState: Boolean

    /**
     * Function called to draw the component.
     *
     * Drawing should be done using the [setPixel] function.
     */
    fun draw(setPixel: DrawFunc)

    fun draw(canvas: Canvas)

    /**
     * Initialize the component position and [context], preparing
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
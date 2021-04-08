package com.heroslender.hmf.core.ui.modifier

interface Placeable {
    val width: Int

    val height: Int

    /**
     * Whether this component should be rendered or not.
     * This value is changed to false if the component is outside the canvas.
     */
    var isVisible: Boolean

    fun placeAt(x: Int, y: Int)
}

abstract class AbstractPlaceable() : Placeable {
    final override var width: Int = 0
        set(value) {
            field = value.coerceIn(constraints.minWidth, constraints.maxWidth)
        }
    final override var height: Int = 0
        set(value) {
            field = value.coerceIn(constraints.minHeight, constraints.maxHeight)
        }

    override var isVisible: Boolean = true

    var constraints: Constraints = Constraints()
}
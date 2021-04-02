package com.heroslender.hmf.core.ui_v2.modifier

interface Placeable {
    val width: Int

    val height: Int

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

    var constraints: Constraints = Constraints()
}
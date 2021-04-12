package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.type.CursorClickModifier

typealias ClickListener = ClickEvent.() -> Unit

/**
 * Listen for clicks on a component.
 */
fun Modifier.clickable(onClick: ClickListener?): Modifier {
    if (onClick == null) {
        return this
    }

    return this then clickableListener(onClick)
}

/**
 * Instantiates a new listener for mouse events.
 */
fun clickableListener(callback: ClickListener): CursorClickModifier {
    return object : CursorClickModifier {
        override fun Placeable.onClick(e: ClickEvent) {
            callback(e)
        }
    }
}

/**
 * Event data for mouse clicks.
 */
data class ClickEvent(
    /**
     * The `x` position where the user clicked in the canvas.
     */
    val x: Int,

    /**
     * The `y` position where the user clicked in the canvas.
     */
    val y: Int,

    /**
     * The click [Type].
     */
    val type: Type,

    /**
     * The clicked component.
     */
    val component: Component,
) {
    enum class Type {
        LEFT_CLICK,
        RIGHT_CLICK
    }
}
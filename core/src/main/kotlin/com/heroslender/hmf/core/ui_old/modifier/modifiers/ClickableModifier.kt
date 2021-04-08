package com.heroslender.hmf.core.ui_old.modifier.modifiers

import com.heroslender.hmf.core.ui_old.Component
import com.heroslender.hmf.core.ui_old.modifier.Modifier
import com.heroslender.hmf.core.ui_old.modifier.ModifierExtra
import com.heroslender.hmf.core.ui_old.modifier.then


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
fun clickableListener(callback: ClickListener): CursorListener {
    return object : CursorListener {
        override fun onClick(event: ClickEvent) {
            callback(event)
        }
    }
}

interface CursorListener : ModifierExtra.Element {

    fun onClick(event: ClickEvent)

    companion object : CursorListener {
        override fun onClick(event: ClickEvent) {}
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
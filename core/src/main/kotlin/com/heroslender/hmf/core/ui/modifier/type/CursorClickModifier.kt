package com.heroslender.hmf.core.ui.modifier.type

import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.Modifier

interface CursorClickModifier : Modifier.Element {

    fun <T>Placeable.onClick(x: Int, y: Int, component: Component, data: T): Boolean
}

/**
 * Event data for mouse clicks.
 */
data class ClickEvent<T>(
    /**
     * The `x` position where the user clicked in the canvas.
     */
    val x: Int,

    /**
     * The `y` position where the user clicked in the canvas.
     */
    val y: Int,

    /**
     * The clicked component.
     */
    val component: Component,

    /**
     * Platform specific custom data appended to the event.
     */
    val data: T,
)

fun ClickEvent<*>.closeMenu() = component.menu.close()

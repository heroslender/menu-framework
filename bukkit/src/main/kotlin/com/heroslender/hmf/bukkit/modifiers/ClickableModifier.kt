package com.heroslender.hmf.bukkit.modifiers

import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.ClickEvent
import com.heroslender.hmf.core.ui.modifier.type.CursorClickModifier
import org.bukkit.entity.Player

typealias ClickListener = BukkitClickEvent.() -> Unit

typealias BukkitClickEvent = ClickEvent<ClickEventData>

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
        override fun Placeable.onClick(x: Int, y: Int, component: Component, data: Any): Boolean {
            val eventData = data as? ClickEventData ?: return false

            ClickEvent(
                x = x,
                y = y,
                component = component,
                data = eventData
            ).callback()

            return true
        }
    }
}

/**
 * The [ClickEvent] data for bukkit servers.
 */
data class ClickEventData(
    /** The type of the click. */
    val type: ClickType,

    /** The player who clicked. */
    val player: Player,
)

/**
 * The type of a click on the menu.
 */
enum class ClickType {
    LEFT_CLICK,
    RIGHT_CLICK
}

/**
 * The type of the click.
 */
val BukkitClickEvent.type: ClickType
    get() = data.type

/**
 * The player who clicked.
 */
val BukkitClickEvent.whoCLicked: Player
    get() = data.player

/**
 * The player who clicked.
 */
val BukkitClickEvent.player: Player
    get() = data.player

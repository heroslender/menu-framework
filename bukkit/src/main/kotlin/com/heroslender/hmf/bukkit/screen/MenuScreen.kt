package com.heroslender.hmf.bukkit.screen

import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.bukkit.screen.chunk.ScreenChunk
import org.bukkit.entity.Player

/**
 * This represents the screen in the world, where the menu
 * will be displayed.
 */
interface MenuScreen {
    val width: Int
    val height: Int
    val chunks: Array<ScreenChunk>

    fun holdsEntityId(entityId: Int): Boolean = chunks.any { it.id == entityId }

    fun spawn(): Unit

    fun spawn(viewers: List<Player>): Unit = chunks.forEach { it.create(viewers.toTypedArray()) }

    fun despawn(): Unit

    fun despawn(viewers: List<Player>): Unit = chunks.forEach { it.destroy(viewers.toTypedArray()) }

    fun update(source: MapCanvas)

    fun updateCursor(player: Player, x: Int, y: Int)
}
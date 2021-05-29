package com.heroslender.hmf.bukkit.screen.chunk

import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import org.bukkit.entity.Player

/**
 * This is a fraction of the screen, or, in the
 * bukkit world, a single map in an item frame.
 */
interface ScreenChunk {
    val id: Int
    val x: Int
    val y: Int
    val z: Int
    val direction: Direction
    val buffer: ByteArray

    fun create(players: Array<Player>)

    fun updateBuffer(
        source: ByteArray,
        sourceWidth: Int,
        offsetX: Int,
        offsetY: Int,
        players: Array<Player>,
    )

    fun sendCursorUpdate(
        cursor: MapIcon? = null,
        players: Array<Player>,
    )

    fun destroy(players: Array<Player>)

    companion object {
        val EMPTY_BUFFER: ByteArray = ByteArray(0)
    }
}
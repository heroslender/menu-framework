package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.map.MapIcon
import com.heroslender.hmf.bukkit.nms.PacketAdapter
import org.bukkit.entity.Player

/**
 * This represents a single item frame/map in the menu.
 */
class MenuChunk(
    val owner: Player,
    val id: Int,
    val buffer: ByteArray = ByteArray(128 * 128),
) {
    private val packetAdapter: PacketAdapter = HmfBukkit.packetAdapter

    fun create(id: Int, x: Int, y: Int, z: Int, direction: Direction) {
        packetAdapter.spawnMapItemFrame(
            id, id, x, y, z, direction, owner
        )
    }

    fun sendCursorUpdate(
        cursor: MapIcon? = null,
    ) {
        packetAdapter.updateMap(
            id,
            0,
            cursor?.let { listOf(cursor) } ?: emptyList(),
            EMPTY_BUFFER,
            0,
            0,
            0,
            0,
            owner,
        )
    }

    fun sendUpdate() {
        packetAdapter.updateMap(
            id,
            0,
            emptyList(),
            buffer,
            0,
            0,
            128,
            128,
            owner
        )
    }

    fun destroy() {
        packetAdapter.destroy(id)
    }

    companion object {
        val EMPTY_BUFFER: ByteArray = ByteArray(0)
    }
}
package com.heroslender.hmf.bukkit.screen.chunk

import com.heroslender.hmf.bukkit.HmfBukkit
import com.heroslender.hmf.bukkit.screen.chunk.ScreenChunk.Companion.EMPTY_BUFFER
import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import com.heroslender.hmf.bukkit.sdk.nms.PacketAdapter
import org.bukkit.entity.Player
import kotlin.math.min

/**
 * A [ScreenChunk] that can only be seen by one player.
 */
class MenuScreenChunk(
    override val id: Int,
    override val x: Int,
    override val y: Int,
    override val z: Int,
    override val direction: Direction,
    override val buffer: ByteArray = ByteArray(128 * 128),
) : ScreenChunk {
    private val packetAdapter: PacketAdapter = HmfBukkit.packetAdapter

    override fun create(players: Array<Player>) {
        packetAdapter.spawnMapItemFrame(
            id, id, x, y, z, direction, players = players
        )
    }

    override fun updateBuffer(source: ByteArray, sourceWidth: Int, offsetX: Int, offsetY: Int, players: Array<Player>) {
        for (x in 0 until min(sourceWidth, 128)) {
            for (y in 0 until min(source.size / sourceWidth, 128)) {
                buffer[x + y * 128] = source[x + offsetX + (y + offsetY) * sourceWidth]
            }
        }

        sendUpdate(players)
    }

    override fun sendCursorUpdate(cursor: MapIcon?, players: Array<Player>) {
        packetAdapter.updateMap(
            id,
            0,
            cursor?.let { listOf(cursor) } ?: emptyList(),
            EMPTY_BUFFER,
            0,
            0,
            0,
            0,
            players,
        )
    }

    private fun sendUpdate(players: Array<Player>) {
        packetAdapter.updateMap(
            id,
            0,
            emptyList(),
            buffer,
            0,
            0,
            128,
            128,
            players
        )
    }

    override fun destroy(players: Array<Player>) {
        packetAdapter.destroy(id, players)
    }
}
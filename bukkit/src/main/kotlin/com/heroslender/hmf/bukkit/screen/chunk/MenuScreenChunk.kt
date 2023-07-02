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
        var minX = 128
        var minZ = 128
        var maxX = 0
        var maxZ = 0

        for (x in 0 until min(sourceWidth, 128)) {
            for (z in 0 until min(source.size / sourceWidth, 128)) {
                val buffIndex = x + z * 128
                val currentColor = buffer[buffIndex]
                val newColor = source[x + offsetX + (z + offsetY) * sourceWidth]
                if (currentColor != newColor) {
                    if (x < minX) {
                        minX = x
                    }
                    if (x > maxX) {
                        maxX = x
                    }
                    if (z < minZ) {
                        minZ = z
                    }
                    if (z > maxZ) {
                        maxZ = z
                    }

                    buffer[buffIndex] = newColor
                }
            }
        }

        sendUpdate(players, minX, minZ, maxX - minX + 1, maxZ - minZ + 1)
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

    private fun sendUpdate(players: Array<Player>, offsetX: Int, offsetZ: Int, sizeX: Int, sizeZ: Int) {
        if (sizeX <= 0 || sizeZ <= 0) {
            return
        }

        packetAdapter.updateMap(
            mapId = id,
            scale = 0,
            icons = emptyList(),
            data = buffer,
            offsetX = offsetX,
            offsetY = offsetZ,
            sizeX = sizeX,
            sizeZ = sizeZ,
            players = players
        )
    }

    override fun destroy(players: Array<Player>) {
        packetAdapter.destroy(id, players)
    }
}
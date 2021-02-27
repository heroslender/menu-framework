package com.heroslender.hmf.bukkit

import org.bukkit.Location
import org.bukkit.entity.Player

class BaseMenu(
    val owner: Player,
    val width: Int = 4,
    val height: Int = 3,
    val direction: Direction = Direction.from(owner).opposite(),
) {
    var startX: Int = 0
    var startY: Int = 0
    var startZ: Int = 0

    private var chunks: Array<MenuChunk> = emptyArray()

    fun setupAndSend() {
        setup()
        send()
    }

    fun setup() {
        val startScreen: Location = owner.location.clone()
            .apply { pitch = 0F }
            .let { it.add(it.direction.multiply(2)) }

        val right = direction.rotateRight()
        val startOffset = -(width / 2 - 1).toDouble()
        startScreen.add(startOffset * right.x, 2.0, startOffset * right.z)

        this.startX = startScreen.blockX
        this.startY = startScreen.blockY
        this.startZ = startScreen.blockZ
    }

    fun send() {
        chunks = Array(width * height) { index ->
            // TODO Fix map ids
            MenuChunk(
                owner = owner,
                id = 9999 + index
            )
        }

        val pd = direction.rotateRight()
        chunks.forEachIndexed { index, chunk ->
            val x = index % width
            val y = index / width

            chunk.create(chunk.id, startX + x * pd.x, startY + -y, startZ + x * pd.z, direction)

            // Fill screen
            for (i in chunk.buffer.indices) {
                chunk.buffer[i] = 20
            }
            chunk.sendUpdate()
        }
    }

    fun destroy() {
        for (chunk in chunks) {
            chunk.destroy()
        }
    }
}
package com.heroslender.hmf.bukkit.screen

import com.heroslender.hmf.bukkit.screen.chunk.MenuScreenChunk
import com.heroslender.hmf.bukkit.screen.chunk.ScreenChunk
import com.heroslender.hmf.bukkit.sdk.Direction
import org.bukkit.Location

inline fun getMenuScreenChunks(
    width: Int,
    height: Int,
    startLocation: Location,
    facing: Direction,
    idSupplier: () -> Int,
): Array<ScreenChunk> {
    val startX = startLocation.blockX
    val startY = startLocation.blockY
    val startZ = startLocation.blockZ
    val left = facing.rotateLeft()

    return Array(width * height) { index ->
        val x = index % width
        val y = index / width

        MenuScreenChunk(
            id = idSupplier(),
            x = startX + x * left.x,
            y = startY + y,
            z = startZ + x * left.z,
            direction = facing
        )
    }
}
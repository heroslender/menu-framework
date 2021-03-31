package com.heroslender.hmf.bukkit.screen

import com.heroslender.hmf.bukkit.MenuOptions
import com.heroslender.hmf.bukkit.screen.chunk.PrivateScreenChunk
import com.heroslender.hmf.bukkit.screen.chunk.ScreenChunk
import com.heroslender.hmf.bukkit.sdk.Direction
import org.bukkit.entity.Player

inline fun privateMenuScreenOf(
    owner: Player,
    opts: MenuOptions,
    width: Int,
    height: Int,
    startX: Int,
    startY: Int,
    startZ: Int,
    facing: Direction,
    idSupplier: () -> Int,
): MenuScreen {
    val left = facing.rotateLeft()

    val chunks: Array<ScreenChunk> = Array(width * height) { index ->
        val x = index % width
        val y = index / width

        PrivateScreenChunk(
            id = idSupplier(),
            owner = owner,
            x = startX + x * left.x,
            y = startY + -y,
            z = startZ + x * left.z,
            direction = facing
        )
    }

    return MenuScreen(
        cursorOpts = opts.cursor,
        width = width,
        height = height,
        chunks = chunks
    )
}
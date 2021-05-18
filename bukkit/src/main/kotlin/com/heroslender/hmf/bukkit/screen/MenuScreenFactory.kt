package com.heroslender.hmf.bukkit.screen

import com.heroslender.hmf.bukkit.MenuOptions
import com.heroslender.hmf.bukkit.screen.chunk.MenuScreenChunk
import com.heroslender.hmf.bukkit.screen.chunk.ScreenChunk
import com.heroslender.hmf.bukkit.screen.tracker.NearbyMenuScreenViewerTracker
import com.heroslender.hmf.bukkit.sdk.Direction
import org.bukkit.Location
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
    val chunks: Array<ScreenChunk> = getMenuScreenChunks(width, height, startX, startY, startZ, facing, idSupplier)

    return PrivateMenuScreen(
        owner = owner,
        cursorOpts = opts.cursor,
        width = width,
        height = height,
        chunks = chunks
    )
}

inline fun publicMenuScreenOf(
    location: Location,
    opts: MenuOptions,
    width: Int,
    height: Int,
    startX: Int,
    startY: Int,
    startZ: Int,
    facing: Direction,
    trackingRange: Int = 25,
    trackedLifetime: Int = 1000,
    idSupplier: () -> Int,
): MenuScreen {
    val chunks: Array<ScreenChunk> = getMenuScreenChunks(width, height, startX, startY, startZ, facing, idSupplier)

    return PublicMenuScreen(
        viewerTracker = NearbyMenuScreenViewerTracker(location, trackingRange, trackedLifetime),
        cursorOpts = opts.cursor,
        width = width,
        height = height,
        chunks = chunks
    )
}

inline fun getMenuScreenChunks(
    width: Int,
    height: Int,
    startX: Int,
    startY: Int,
    startZ: Int,
    facing: Direction,
    idSupplier: () -> Int,
): Array<ScreenChunk> {

    val left = facing.rotateLeft()

    return Array(width * height) { index ->
        val x = index % width
        val y = index / width

        MenuScreenChunk(
            id = idSupplier(),
            x = startX + x * left.x,
            y = startY + -y,
            z = startZ + x * left.z,
            direction = facing
        )
    }
}
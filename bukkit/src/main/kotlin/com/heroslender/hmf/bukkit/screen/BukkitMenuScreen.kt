package com.heroslender.hmf.bukkit.screen

import com.heroslender.hmf.bukkit.MenuOptions
import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.bukkit.screen.chunk.ScreenChunk
import com.heroslender.hmf.bukkit.screen.tracker.MenuScreenViewerTracker
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import com.heroslender.hmf.bukkit.utils.clampByte
import org.bukkit.entity.Player

/**
 * This represents the screen in the world, where the menu
 * will be displayed.
 */
class BukkitMenuScreen(
    override val viewerTracker: MenuScreenViewerTracker,
    private val cursorOpts: MenuOptions.CursorOptions,
    override val width: Int,
    override val height: Int,
    override val chunks: Array<ScreenChunk>,
) : MenuScreen {
    private val cursor = Cursor(width * 128 / 2, height * 128 / 2, MapIcon.Type.GREEN_POINTER)

    override fun spawn() = chunks.forEach { it.create(viewerTracker.viewers) }

    override fun despawn() = chunks.forEach { it.destroy(viewerTracker.viewers) }

    override fun update(source: MapCanvas) {
        chunks.forEachIndexed { index, chunk ->
            val startX = index % width * 128
            val startY = index / width * 128

            chunk.updateBuffer(source.buffer, source.width, startX, startY, viewerTracker.viewers)
        }
    }

    private var prevCursorChunk: ScreenChunk? = null
    override fun updateCursor(player: Player, x: Int, y: Int) {
        if (x == cursor.x && y == cursor.y) {
            return
        }

        val chunkIndex = x / 128 + y / 128 * width
        if (chunkIndex >= chunks.size) {
            // Outside the menu? This should not happen
            return
        }

        val chunk = chunks[chunkIndex]
        val mapX = x % 128 * 2 - 128 + cursorOpts.offsetX
        val mapY = y % 128 * 2 - 128 + cursorOpts.offsetY

        chunk.sendCursorUpdate(MapIcon(
            clampByte(mapX),
            clampByte(mapY),
            cursorOpts.iconRotation,
            cursorOpts.iconType,
        ), viewerTracker.viewers)

        // Remove the cursor from previous map
        if (prevCursorChunk !== chunk) {
            prevCursorChunk?.sendCursorUpdate(players = viewerTracker.viewers)
        }

        prevCursorChunk = chunk
    }

    data class Cursor(
        var x: Int,
        var y: Int,
        var type: MapIcon.Type,
    )
}
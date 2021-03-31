package com.heroslender.hmf.bukkit.screen

import com.heroslender.hmf.bukkit.MenuOptions
import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.bukkit.screen.chunk.ScreenChunk
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import com.heroslender.hmf.bukkit.utils.clampByte

/**
 * This represents the screen in the world, where the menu
 * will be displayed.
 */
class MenuScreen(
    val cursorOpts: MenuOptions.CursorOptions,
    private val width: Int,
    private val height: Int,
    val chunks: Array<ScreenChunk>,
) {
    val cursor = Cursor(width * 128 / 2, height * 128 / 2, MapIcon.Type.GREEN_POINTER)

    fun spawn() {
        for (chunk in chunks) {
            chunk.create()
        }
    }

    fun despawn() {
        for (chunk in chunks) {
            chunk.destroy()
        }
    }

    /**
     * Sends the updated canvas to the clients.
     */
    fun update(source: MapCanvas) {
        chunks.forEachIndexed { index, chunk ->
            val startX = index % width * 128
            val startY = index / width * 128

            chunk.updateBuffer(source.buffer, source.width, startX, startY)
        }
    }

    private var prevCursorChunk: ScreenChunk? = null
    fun updateCursor(x: Int, y: Int) {
        if (x == cursor.x && y == cursor.y) {
            return
        }

        val chunkIndex = x % 128 + y % 128 * width
        if (chunkIndex >= chunks.size) {
            // Outside the menu? This should not happen
            return
        }

        val chunk = chunks[chunkIndex]
        val mapX = x * 2 - 128 + cursorOpts.offsetX
        val mapY = y * 2 - 128 + cursorOpts.offsetY

        chunk.sendCursorUpdate(MapIcon(
            clampByte(mapX),
            clampByte(mapY),
            cursorOpts.iconRotation,
            cursorOpts.iconType,
        ))

        // Remove the cursor from previous map
        if (prevCursorChunk !== chunk) {
            prevCursorChunk?.sendCursorUpdate()
        }

        prevCursorChunk = chunk
    }

    data class Cursor(
        var x: Int,
        var y: Int,
        var type: MapIcon.Type,
    )
}
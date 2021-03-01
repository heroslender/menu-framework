package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.listeners.MenuListeners
import com.heroslender.hmf.bukkit.map.Color
import com.heroslender.hmf.bukkit.map.MapIcon
import com.heroslender.hmf.bukkit.utils.BoundingBox
import com.heroslender.hmf.bukkit.utils.clamp
import com.heroslender.hmf.bukkit.utils.clampByte
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.plugin.java.JavaPlugin

open class BaseMenu(
    val owner: Player,
    val width: Int = 4,
    val height: Int = 3,
    val direction: Direction = Direction.from(owner).opposite(),
    val opts: MenuOptions = MenuOptions(),
) {
    var startX: Int = 0
    var startY: Int = 0
    var startZ: Int = 0

    private var chunks: Array<MenuChunk> = emptyArray()

    private var boundingBox: BoundingBox = BoundingBox.EMPTY

    fun setupAndSend() {
        setup()
        send()
    }

    fun setup() {
        val startScreen: Location = owner.location.clone()
            .apply { pitch = 0F }
            .let { it.add(it.direction.multiply(2)) }

        val right = direction.rotateLeft()
        val startOffset = -(width / 2 - 1).toDouble()
        startScreen.add(startOffset * right.x, 2.0, startOffset * right.z)

        this.startX = startScreen.blockX
        this.startY = startScreen.blockY
        this.startZ = startScreen.blockZ

        val frameDirection = direction//.opposite()
        val left = frameDirection.rotateLeft()
        var centerX = (startX + .5)
        centerX -= frameDirection.x * 0.46875
        centerX += left.x * .5
        var centerZ = (startZ + .5)
        centerZ -= frameDirection.z * 0.46875
        centerZ += left.z * .5
        val offX = if (frameDirection.x != 0) .03125 else left.x.toDouble()
        val offZ = if (frameDirection.z != 0) .03125 else left.z.toDouble()

        val bbStartX = centerX - offX
        val bbStartZ = centerZ - offZ

        val xBound = if (frameDirection.x == 0) {
            bbStartX + width * right.x
        } else {
            // Screen thickness
            bbStartX + .0625
        }

        val zBound = if (frameDirection.z == 0) {
            bbStartZ + width * right.z
        } else {
            bbStartZ + .0625
        }
        boundingBox = BoundingBox.of(
            bbStartX,
            startY - (height - 1.0),
            bbStartZ,
            xBound,
            startY + 1.0,
            zBound,
        )
    }

    fun send() {
        chunks = Array(width * height) { index ->
            // TODO Fix map ids
            MenuChunk(
                owner = owner,
                id = 9999 + index
            )
        }

        val pd = direction.rotateLeft()
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

        registerMouseCursor()

        HmfBukkit.manager.add(this)
    }

    fun destroy() {
        Bukkit.getScheduler().cancelTask(cursorTaskId)

        for (chunk in chunks) {
            chunk.destroy()
        }
    }

    var cursorTaskId: Int = 0
    private fun registerMouseCursor() {
        var prevId = -1

        // Mouse render
        cursorTaskId = scheduleAsyncTimer(JavaPlugin.getProvidingPlugin(this::class.java), opts.cursor.updateDelay) {
            raytrace { x, y ->
                val index = clamp(x.toInt(), 0, width - 1) + clamp(y.toInt(), 0, height - 1) * width
                if (index >= chunks.size) {
                    // Outside the menu? This should not happen
                    return@raytrace
                }

                val chunk = chunks[index]
                val mapX = ((x % 1) * 256).toInt() - 118
                val mapY = ((y % 1) * 256).toInt() - 118

                chunk.sendCursorUpdate(MapIcon(
                    clampByte(mapX),
                    clampByte(mapY),
                    opts.cursor.iconRotation,
                    opts.cursor.iconType,
                ))

                // Remove the cursor from previous map
                if (prevId >= 0 && prevId != index) {
                    chunks[prevId].sendCursorUpdate()
                }

                prevId = index
            }
        }
    }

    fun onInteract(action: Action) {
        raytrace { x, y ->
            val index = clamp(x.toInt(), 0, width - 1) + clamp(y.toInt(), 0, height - 1) * width
            if (index >= chunks.size) {
                // Outside the menu? This should not happen
                return@raytrace
            }

            val chunk = chunks[index]
            val mapX = ((x % 1) * 128).toInt()
            val mapY = ((y % 1) * 128).toInt()

            val color = when (action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> Color.BLACK_1.id
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> Color.WHITE_11.id
                else -> Color.GREEN_18.id
            }

            chunk.buffer[mapX + mapY * 128] = color
            chunk.sendUpdate()
        }
    }

    private inline fun raytrace(onIntersect: (x: Double, y: Double) -> Unit) {
        val intersection = boundingBox.rayTrace(
            start = owner.eyeLocation.toVector(),
            direction = owner.location.direction,
            maxDistance = opts.maxInteractDistance
        ) ?: return

        val rd = direction.rotateLeft()
        val x = if (rd.x != 0) {
            (intersection.x - boundingBox.minX) * rd.x
        } else {
            (intersection.z - boundingBox.minZ) * rd.z
        }
        val y = boundingBox.maxY - intersection.y

        onIntersect(if (x < 0) x + width else x, y)
    }
}
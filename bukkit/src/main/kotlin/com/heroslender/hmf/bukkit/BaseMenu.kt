package com.heroslender.hmf.bukkit

import androidx.compose.runtime.CompositionLocalProvider
import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.bukkit.modifiers.ClickEventData
import com.heroslender.hmf.bukkit.modifiers.ClickType
import com.heroslender.hmf.bukkit.screen.BukkitMenuScreen
import com.heroslender.hmf.bukkit.screen.MenuScreen
import com.heroslender.hmf.bukkit.screen.getMenuScreenChunks
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import com.heroslender.hmf.bukkit.utils.BoundingBox
import com.heroslender.hmf.bukkit.utils.boundingBoxOf
import com.heroslender.hmf.core.compose.*
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.size
import org.bukkit.entity.Player
import java.util.*

abstract class BaseMenu(
    /** Menu specific options */
    val opts: MenuOptions,
    /** The manager that will handle this menu */
    val manager: BukkitMenuManager,
) : BukkitMenu {

    var screen: MenuScreen? = null
    final override var boundingBox: BoundingBox = BoundingBox.EMPTY

    private var clickHandler: ClickHandler? = null

    fun hasEntityId(id: Int): Boolean {
        return screen?.holdsEntityId(id) ?: false
    }

    init {
        this.boundingBox = calculateBoundingBox()
    }

    fun send() {
        val chunks = manager.withEntityIdFactory { nextEntityId ->
            manager.register(this)

            getMenuScreenChunks(opts.width, opts.height, opts.location, opts.direction, nextEntityId)
        }

        val screen = BukkitMenuScreen(
            viewerTracker = opts.viewerTracker.make(),
            cursorOpts = opts.cursor,
            width = opts.width,
            height = opts.height,
            chunks = chunks
        )
        this.screen = screen

        screen.viewerTracker.tick()
        screen.spawn()

        val canvas = MapCanvas(opts.width * 128, opts.height * 128)
        ComposeMenu().apply {
            updateHandler = {
                println("Updating...")
                screen.update(canvas)
                println(canvas.buffer.contentToString())
            }

            rootNode.modifier = Modifier.size(canvas.width, canvas.height)
            rootNode.canvas = canvas
//
            start {
                clickHandler = LocalClickHandler.current
                CompositionLocalProvider(LocalCanvas provides canvas) {
                    println("Passing canvas")
                    CompositionLocalProvider(LocalImageProvider provides manager.imageProvider) {
                        getUi()
                    }
                }
            }
        }
    }

    override fun close() {
        dispose()
        destroy()
    }

    fun destroy() {
        screen?.despawn()
    }

    fun dispose() {
        manager.unregister(this)
    }

    override fun tickCursor(player: Player, x: Int, y: Int) {
        screen?.updateCursor(player, x, y)
    }

    override fun onInteract(player: Player, action: PacketInterceptor.Action, x: Int, y: Int) {
        val type = when (action) {
            PacketInterceptor.Action.RIGHT_CLICK ->
                ClickType.RIGHT_CLICK

            else ->
                ClickType.LEFT_CLICK
        }

        println("click to " + clickHandler)
        clickHandler?.processClick(x, y, ClickEventData(type, player))
    }

    private fun calculateBoundingBox(): BoundingBox {
        val startX = opts.location.blockX
        val startY = opts.location.blockY
        val startZ = opts.location.blockZ
        val direction = opts.direction
        val width = opts.width
        val height = opts.height
        val bbStartX: Double
        val bbEndX: Double
        when (direction.x) {
            -1 -> {
                bbStartX = startX + .9375
                bbEndX = bbStartX
            }

            1 -> {
                bbStartX = startX + .0625
                bbEndX = bbStartX
            }

            0 -> { // Equals to 0
                val left = direction.rotateLeft()
                bbStartX = if (left.x == -1) (startX + 1).toDouble() else startX.toDouble()
                bbEndX = bbStartX + width * left.x
            }

            else -> {
                bbStartX = startX.toDouble()
                bbEndX = bbStartX
            }
        }

        val bbStartZ: Double
        val bbEndZ: Double
        when (direction.z) {
            -1 -> {
                bbStartZ = startZ + .9375
                bbEndZ = bbStartZ
            }

            1 -> {
                bbStartZ = startZ + .0625
                bbEndZ = bbStartZ
            }

            0 -> {
                val left = direction.rotateLeft()
                bbStartZ = if (left.z == -1) (startZ + 1).toDouble() else startZ.toDouble()
                bbEndZ = bbStartZ + width * left.z
            }

            else -> {
                bbStartZ = startZ.toDouble()
                bbEndZ = bbStartZ
            }
        }

        return boundingBoxOf(
            bbStartX,
            startY + 1.0,
            bbStartZ,
            bbEndX,
            startY + height + 1.0,
            bbEndZ,
        )
    }
}
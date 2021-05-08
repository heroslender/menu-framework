package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.bukkit.screen.MenuScreen
import com.heroslender.hmf.bukkit.screen.privateMenuScreenOf
import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import com.heroslender.hmf.bukkit.utils.BoundingBox
import com.heroslender.hmf.bukkit.utils.boundingBoxOf
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class BaseMenu(
    val owner: Player,
    val width: Int = 4,
    val height: Int = 3,
    val direction: Direction = Direction.from(owner).opposite(),
    val manager: BukkitMenuManager,
    override val context: BukkitContext = Context(manager, MapCanvas(width * 128, height * 128)),
    val opts: MenuOptions = MenuOptions(),
) : BukkitMenu {
    var startX: Int = 0
    var startY: Int = 0
    var startZ: Int = 0

    private var screen: MenuScreen? = null
    final override var boundingBox: BoundingBox = BoundingBox.EMPTY

    fun hasEntityId(id: Int): Boolean {
        return screen?.chunks?.any { it.id == id } ?: false
    }

    init {
        val startScreen: Location = owner.location.clone()
            .apply { pitch = 0F }
            .let { it.add(it.direction.multiply(2)) }

        val left = direction.rotateLeft()
        val startOffset = -(width / 2 - 1).toDouble()
        startScreen.add(startOffset * left.x, 2.0, startOffset * left.z)

        this.startX = startScreen.blockX
        this.startY = startScreen.blockY
        this.startZ = startScreen.blockZ

        this.boundingBox = calculateBoundingBox()
    }

    fun send() {
        this.screen = manager.withEntityIdFactory { nextEntityId ->
            manager.add(this)

            privateMenuScreenOf(
                owner,
                opts,
                width,
                height,
                startX,
                startY,
                startZ,
                direction,
                nextEntityId
            )
        }

        screen?.spawn()

        context.onUpdate {
            screen?.update(context.canvas)
        }

        render()
    }

    fun destroy() {
        screen?.despawn()

        manager.remove(owner)
    }

    override fun tickCursor(player: Player, x: Int, y: Int) {
        screen?.updateCursor(x, y)
    }

    override fun onInteract(player: Player, action: PacketInterceptor.Action, x: Int, y: Int) {
        val type = when (action) {
            PacketInterceptor.Action.RIGHT_CLICK ->
                ClickEvent.Type.RIGHT_CLICK
            else ->
                ClickEvent.Type.LEFT_CLICK
        }

        context.handleClick(x, y, type)
    }

    private fun calculateBoundingBox(): BoundingBox {
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
            startY - (height - 1.0),
            bbStartZ,
            bbEndX,
            startY + 1.0,
            bbEndZ,
        )
    }
}
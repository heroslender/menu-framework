package com.heroslender.hmf.bukkit.manager.impl

import com.heroslender.hmf.bukkit.BaseMenu
import com.heroslender.hmf.bukkit.HmfBukkit
import com.heroslender.hmf.bukkit.listeners.MenuListener
import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import com.heroslender.hmf.bukkit.utils.scheduleAsyncTimer
import com.heroslender.hmf.core.ImageProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin
import java.util.concurrent.atomic.AtomicInteger

@Suppress("MemberVisibilityCanBePrivate")
class BukkitMenuManagerImpl(
    private val plugin: Plugin,
    val opts: Options = Options(),
    override val imageProvider: ImageProvider = BukkitImageProviderImpl(),
) : BukkitMenuManager, PacketInterceptor.PacketInterceptorHandler {
    override val handlerId: String = "hmf_packet_handler_${handlerIdCounter.getAndIncrement()}"

    private var cursorTaskId: Int = 0
    private var renderTaskId: Int = 0

    private var menuListener: Listener? = null
    private var menuClickListener: Listener? = null

    private val interactCooldown: MutableMap<Player, Long> = mutableMapOf()

    private val _menus: MutableList<BaseMenu> = mutableListOf()
    val menus: List<BaseMenu>
        get() = _menus

    init {
        launchCursorTask(opts.cursorUpdateDelay)

        if (opts.listenClicks) {
            this.menuClickListener = object : Listener {
                @EventHandler
                fun onInteract(e: PlayerInteractEvent) {
                    // Cancel the event if the interaction was handled by a menu
                    e.isCancelled = handleInteraction(e.player, e.action)
                }
            }.also { Bukkit.getPluginManager().registerEvents(it, plugin) }
        }

        this.menuListener = MenuListener(this).also { listener ->
            Bukkit.getPluginManager().registerEvents(listener, plugin)
        }
    }

    override fun dispose() {
        HandlerList.unregisterAll(menuListener)
        HandlerList.unregisterAll(menuClickListener)

        Bukkit.getScheduler().cancelTask(cursorTaskId)
        Bukkit.getScheduler().cancelTask(renderTaskId)

        for (player in Bukkit.getOnlinePlayers()) {
            HmfBukkit.packetAdapter.removePacketInterceptor(player, this)
        }
    }

    override fun register(menu: BaseMenu) {
        while (menus.size > 4) {
            _menus[0].close()
        }

        _menus.add(menu)
    }

    override fun unregister(menu: BaseMenu) {
        _menus.remove(menu)
    }

    private val entityIdMutex: Any = Any()

    override fun nextEntityId(): Int = withEntityIdFactory { next -> next() }

    override fun <R> withEntityIdFactory(factory: (nextEntityId: () -> Int) -> R): R = entityIdFactory(factory)

    /**
     * Executes [factory] while holding a lock on the [entityIdMutex].
     */
    private inline fun <R> entityIdFactory(factory: (nextEntityId: () -> Int) -> R): R = synchronized(entityIdMutex) {
        val usedIds: MutableList<Int> = mutableListOf()

        factory {
            var id = opts.firstEntityId

            while (usedIds.contains(id) || menus.any { it.hasEntityId(id) }) {
                id++
            }

            usedIds += id
            return@factory id
        }
    }

    override fun handleInteraction(player: Player, entityId: Int, action: PacketInterceptor.Action): Boolean {
        if (!opts.listenClicks || entityId < opts.firstEntityId) {
            return false
        }

        return raytraceInteraction(player) raytrace@{ menu, x, y ->
            if (!player.tryInteract()) {
                return@raytrace false
            }

            menu.onInteract(player.player, action, x, y)
            return@raytrace true
        }
    }

    fun handleInteraction(player: Player, action: Action): Boolean {
        if (!opts.listenClicks) {
            return false
        }

        return raytraceInteraction(player) raytrace@{ menu, x, y ->
            if (!player.tryInteract()) {
                return@raytrace false
            }

            val act = when (action) {
                Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR ->
                    PacketInterceptor.Action.RIGHT_CLICK
                else ->
                    PacketInterceptor.Action.LEFT_CLICK
            }

            menu.onInteract(player.player, act, x, y)
            return@raytrace true
        }
    }

    private fun launchCursorTask(delay: Long) {
        if (delay <= 0) return

        cursorTaskId = scheduleAsyncTimer(plugin, delay) {
            for (menu in menus) {
                val loc = menu.opts.location

                for (player in loc.world.players) {
                    if (loc.distanceSquared(player.location) < opts.maxInteractDistanceSqr) {
                        val viewerTracker = menu.screen?.viewerTracker ?: continue

                        if (viewerTracker.isTracked(player) && viewerTracker.canInteract(player)) {
                            menu.raytrace(player) { x, y ->
                                menu.tickCursor(player, x, y)
                            }
                        }
                    }
                }
            }
        }
    }

    inline fun raytraceInteraction(player: Player, onIntersect: (menu: BaseMenu, x: Int, y: Int) -> Boolean): Boolean {
        for (menu in menus) {
            if (menu.opts.location.distanceSquared(player.location) < opts.maxInteractDistanceSqr) {
                val viewerTracker = menu.screen?.viewerTracker ?: continue

                if (viewerTracker.isTracked(player) && viewerTracker.canInteract(player)) {
                    menu.raytrace(player) { x, y ->
                        val handled = onIntersect(menu, x, y)

                        if (handled) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    inline fun BaseMenu.raytrace(player: Player, onIntersect: (x: Int, y: Int) -> Unit): Boolean {
        val intersection = boundingBox.rayTrace(
            start = player.eyeLocation.toVector(),
            direction = player.location.direction,
            maxDistance = this@BukkitMenuManagerImpl.opts.maxInteractDistance
        ) ?: return false

        val rd = opts.direction.rotateLeft()
        val x = if (rd.x != 0) {
            (intersection.x - boundingBox.minX) * rd.x
        } else {
            (intersection.z - boundingBox.minZ) * rd.z
        }

        val y = boundingBox.maxY - intersection.y

        onIntersect(
            ((if (x < 0) x + opts.width else x) * 128).toInt(),
            (y * 128).toInt()
        )
        return true
    }

    private fun Player.tryInteract(): Boolean {
        val nextInteraction = interactCooldown[this] ?: 0
        val now = System.currentTimeMillis()
        if (nextInteraction > now) {
            return false
        }

        interactCooldown[this] = now + INTERACT_COOLDOWN
        return true
    }

    data class Options(
        /**
         * The first entity ID to be used when sending the
         * ItemFrame spawn packet to the player.
         */
        val firstEntityId: Int = 9999,

        /**
         * Whether this manager should listen for menu clicks.
         */
        val listenClicks: Boolean = true,

        /**
         * The delay, in game ticks, between each render.
         *
         * If set to 0(zero) or less, render updates will be disabled
         */
        val renderUpdateDelay: Long = 10,

        /**
         * The delay, in game ticks, between each cursor update.
         *
         * If set to 0(zero) or less, cursor updates will be disabled
         */
        val cursorUpdateDelay: Long = 2,

        /**
         * The maximum distance the player can interact with
         * the menu. This is for the cursor movement and clicks.
         */
        val maxInteractDistance: Double = 8.0,
    ) {
        val maxInteractDistanceSqr: Double = maxInteractDistance * maxInteractDistance
    }

    companion object {
        const val INTERACT_COOLDOWN = 200

        private val handlerIdCounter: AtomicInteger = AtomicInteger()
    }
}
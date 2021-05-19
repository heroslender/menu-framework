package com.heroslender.hmf.bukkit.manager.impl

import com.heroslender.hmf.bukkit.BaseMenu
import com.heroslender.hmf.bukkit.listeners.MenuClickListener
import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
import com.heroslender.hmf.bukkit.manager.ImageManager
import com.heroslender.hmf.bukkit.manager.UserManager
import com.heroslender.hmf.bukkit.models.User
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import com.heroslender.hmf.bukkit.utils.scheduleAsyncTimer
import com.heroslender.hmf.core.ui.components.Image
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.plugin.Plugin

@Suppress("MemberVisibilityCanBePrivate")
class BukkitMenuManagerImpl(
    private val plugin: Plugin,
    val opts: Options = Options(),
    val imageManager: ImageManager = ImageManagerImpl(),
    val userManager: UserManager = UserManagerImpl(plugin),
) : BukkitMenuManager, PacketInterceptor.PacketInterceptorHandler {

    private var cursorTaskId: Int = 0
    private var renderTaskId: Int = 0

    private var menuClickListener: Listener? = null

    init {
        launchCursorTask(opts.cursorUpdateDelay)
        launchRenderTask(opts.renderUpdateDelay)

        if (opts.listenClicks) {
            this.menuClickListener = MenuClickListener(this).also { listener ->
                Bukkit.getPluginManager().registerEvents(listener, plugin)
            }
        }
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

            while (usedIds.contains(id) || userManager.users.any { it.menu.hasEntityId(id) }) {
                id++
            }

            usedIds += id
            return@factory id
        }
    }

    override fun get(owner: Player): BaseMenu? {
        return userManager.get(owner)?.menu
    }

    override fun remove(owner: Player): BaseMenu? {
        return userManager.remove(owner)?.menu
    }

    override fun add(menu: BaseMenu) {
        userManager.create(menu.owner, menu, this)
    }

    override fun getImage(url: String, width: Int, height: Int, cached: Boolean): Image? =
        imageManager.getImage(url, width, height, cached)

    override fun handleInteraction(player: Player, entityId: Int, action: PacketInterceptor.Action): Boolean {
        if (entityId < opts.firstEntityId) {
            return false
        }

        val user = userManager.get(player) ?: return false
        return handleInteraction(user, action)
    }

    fun handleInteraction(player: Player, action: Action): Boolean {
        val user = userManager.get(player) ?: return false

        val act = when (action) {
            Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR ->
                PacketInterceptor.Action.RIGHT_CLICK
            else ->
                PacketInterceptor.Action.LEFT_CLICK
        }

        return handleInteraction(user, act)
    }

    fun handleInteraction(user: User, action: PacketInterceptor.Action): Boolean {
        if (!opts.listenClicks) {
            return false
        }

        if (!user.tryInteract()) {
            return false
        }

        val menu = user.menu
        return menu.raytrace(user.player) { x, y ->
            menu.onInteract(user.player, action, x, y)
        }
    }

    private fun launchCursorTask(delay: Long) {
        if (delay <= 0) return

        cursorTaskId = scheduleAsyncTimer(plugin, delay) {
            for (user in userManager.users) {
                val menu = user.menu
                menu.raytrace(user.player) { x, y ->
                    menu.tickCursor(user.player, x, y)
                }
            }
        }
    }

    private fun launchRenderTask(delay: Long) {
        if (delay <= 0) return

        renderTaskId = scheduleAsyncTimer(plugin, delay) {
            for (user in userManager.users) {
                val menu = user.menu
                val screen = menu.screen ?: continue

                screen.viewerTracker.tick()
                menu.also { render(it) }
            }
        }
    }

    inline fun BaseMenu.raytrace(player: Player, onIntersect: (x: Int, y: Int) -> Unit): Boolean {
        val intersection = boundingBox.rayTrace(
            start = player.eyeLocation.toVector(),
            direction = player.location.direction,
            maxDistance = this@BukkitMenuManagerImpl.opts.maxInteractDistance
        ) ?: return false

        val rd = direction.rotateLeft()
        val x = if (rd.x != 0) {
            (intersection.x - boundingBox.minX) * rd.x
        } else {
            (intersection.z - boundingBox.minZ) * rd.z
        }

        val y = boundingBox.maxY - intersection.y

        onIntersect(
            ((if (x < 0) x + width else x) * 128).toInt(),
            (y * 128).toInt()
        )
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
         */
        val renderUpdateDelay: Long = 10,

        /**
         * The delay, in game ticks, between each cursor update.
         */
        val cursorUpdateDelay: Long = 2,

        /**
         * The maximum distance the player can interact with
         * the menu. This is for the cursor movement and clicks.
         */
        val maxInteractDistance: Double = 5.0,
    )
}
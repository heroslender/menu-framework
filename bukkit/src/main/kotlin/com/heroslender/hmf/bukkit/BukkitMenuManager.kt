package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.image.ImageManager
import com.heroslender.hmf.bukkit.listeners.MenuClickListener
import com.heroslender.hmf.bukkit.listeners.MenuListeners
import com.heroslender.hmf.bukkit.utils.scheduleAsyncTimer
import com.heroslender.hmf.core.MenuManager
import com.heroslender.hmf.core.ui.components.Image
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class BukkitMenuManager(
    val plugin: Plugin,
    val opts: Options = Options(),
    private val imageManager: ImageManager = ImageManager(),
) : MenuManager<Player, BaseMenu> {
    private val menus: MutableList<BaseMenu> = mutableListOf()

    private var cursorTaskId: Int = 0
    private var renderTaskId: Int = 0

    private var menuClickListener: Listener? = null
    private var menuListeners: Listener? = null

    init {
        if (opts.cursorUpdateDelay > 0) {
            launchCursorTask()
        }

        if (opts.listenClicks) {
            this.menuClickListener = MenuClickListener(this).also { listener ->
                Bukkit.getPluginManager().registerEvents(listener, plugin)
            }
        }

        this.menuListeners = MenuListeners(this).also { listener ->
            Bukkit.getPluginManager().registerEvents(listener, plugin)
        }

        launchRenderTask()
    }

    override fun get(owner: Player): BaseMenu? {
        return menus.firstOrNull { it.owner === owner }
    }

    override fun remove(owner: Player): BaseMenu? {
        return get(owner)?.also { menus.remove(it) }
    }

    override fun add(menu: BaseMenu) {
        remove(menu.owner)?.destroy()

        menus.add(menu)
    }

    override fun getImage(url: String, width: Int, height: Int, cached: Boolean): Image? =
        imageManager.getImage(url, width, height, cached)

    private fun launchCursorTask() {
        cursorTaskId = scheduleAsyncTimer(plugin, opts.cursorUpdateDelay) {
            for (menu in menus) {
                menu.tickCursor()
            }
        }
    }

    private fun launchRenderTask() {
        renderTaskId = scheduleAsyncTimer(plugin, 10) {
            for (menu in menus) {
                render(menu)
            }
        }
    }

    data class Options(
        val listenClicks: Boolean = true,
        val cursorUpdateDelay: Long = 2,
        val maxInteractDistance: Double = 5.0,
    )
}
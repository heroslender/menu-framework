package com.heroslender.hmf.bukkit.listeners

import com.heroslender.hmf.bukkit.HmfBukkit
import com.heroslender.hmf.bukkit.MenuManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

object MenuListeners : Listener {
    private val manager: MenuManager = HmfBukkit.manager

    private var registered = false

    fun ensureRegistered() {
        if (!registered) {
            Bukkit.getServer().pluginManager.registerEvents(this, JavaPlugin.getProvidingPlugin(this::class.java))
            registered = true
        }
    }

    @EventHandler
    fun onDisconnect(e: PlayerQuitEvent) {
        manager.remove(e.player)
    }

    @EventHandler
    fun onKick(e: PlayerKickEvent) {
        manager.remove(e.player)
    }

    @EventHandler(ignoreCancelled = false)
    fun onInteract(e: PlayerInteractEvent) {
        val menu = manager.getOwned(e.player) ?: return

        e.isCancelled = true
        menu.onInteract(e.action)
    }
}
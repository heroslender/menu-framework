package com.heroslender.hmf.bukkit.listeners

import com.heroslender.hmf.bukkit.BukkitMenuManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

class MenuListeners(
    private val manager: BukkitMenuManager,
) : Listener {

    @EventHandler
    fun onDisconnect(e: PlayerQuitEvent) {
        manager.remove(e.player)
    }

    @EventHandler
    fun onKick(e: PlayerKickEvent) {
        manager.remove(e.player)
    }
}
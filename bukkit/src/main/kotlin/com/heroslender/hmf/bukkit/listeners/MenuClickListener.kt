package com.heroslender.hmf.bukkit.listeners

import com.heroslender.hmf.bukkit.BukkitMenuManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class MenuClickListener(
    private val manager: BukkitMenuManager,
) : Listener {

    @EventHandler(ignoreCancelled = false)
    fun onInteract(e: PlayerInteractEvent) {
        val menu = manager.get(e.player) ?: return

        e.isCancelled = menu.onInteract(e.action)
    }
}
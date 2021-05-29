package com.heroslender.hmf.bukkit.listeners

import com.heroslender.hmf.bukkit.manager.impl.BukkitMenuManagerImpl
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class MenuClickListener(
    private val manager: BukkitMenuManagerImpl,
) : Listener {

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        e.isCancelled = !manager.handleInteraction(e.player, e.action)
    }
}
package com.heroslender.hmf.bukkit.listeners

import com.heroslender.hmf.bukkit.manager.impl.BukkitMenuManagerImpl
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class MenuClickListener(
    private val manager: BukkitMenuManagerImpl,
) : Listener {

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        manager.handleInteraction(e.player, e.action)
    }
}
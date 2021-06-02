package com.heroslender.hmf.bukkit.listeners

import com.heroslender.hmf.bukkit.HmfBukkit
import com.heroslender.hmf.bukkit.manager.impl.BukkitMenuManagerImpl
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class MenuListener(private val menuManager: BukkitMenuManagerImpl) : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        HmfBukkit.packetAdapter.addPacketInterceptor(e.player, menuManager)
    }
}

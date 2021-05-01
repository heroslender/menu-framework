package com.heroslender.hmf.bukkit.listeners

import com.heroslender.hmf.bukkit.BukkitMenuManager
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class MenuClickListener(
    private val manager: BukkitMenuManager,
) : Listener {

    @EventHandler(ignoreCancelled = false)
    fun onInteract(e: PlayerInteractEvent) {
        val menu = manager.get(e.player) ?: return

        val action = when (e.action) {
            Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR ->
                PacketInterceptor.Action.RIGHT_CLICK
            else ->
                PacketInterceptor.Action.LEFT_CLICK
        }

        e.isCancelled = menu.onInteract(action)
    }
}
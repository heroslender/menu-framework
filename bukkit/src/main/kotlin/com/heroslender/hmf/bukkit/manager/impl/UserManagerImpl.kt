package com.heroslender.hmf.bukkit.manager.impl

import com.heroslender.hmf.bukkit.HmfBukkit
import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
import com.heroslender.hmf.bukkit.manager.UserManager
import com.heroslender.hmf.bukkit.models.User
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import com.heroslender.hmf.bukkit.utils.ignore
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class UserManagerImpl(
    plugin: Plugin,
) : UserManager, Listener {
    override val users: MutableList<User> = mutableListOf()

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun get(player: Player): User? = users.firstOrNull { it.player == player }

    override fun getOrCreate(player: Player, handler: PacketInterceptor.PacketInterceptorHandler): User =
        get(player) ?: userOf(player, handler).also { users.add(it) }

    override fun remove(player: Player): User? {
        return get(player)?.also { user ->
            user.menu?.destroy()
            user.menu = null
            users.remove(user)
        }
    }

    private fun userOf(player: Player, handler: PacketInterceptor.PacketInterceptorHandler): User = User(player).also {
//        HmfBukkit.packetAdapter.addPacketInterceptor(player, handler)
    }

    private fun onDisconnect(player: Player): Unit = remove(player).ignore()

    @EventHandler(ignoreCancelled = true)
    fun onDisconnect(e: PlayerQuitEvent) {
        onDisconnect(e.player)
    }

    @EventHandler
    fun onKick(e: PlayerKickEvent) {
        onDisconnect(e.player)
    }
}
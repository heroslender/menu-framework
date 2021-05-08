package com.heroslender.hmf.bukkit.manager

import com.heroslender.hmf.bukkit.BaseMenu
import com.heroslender.hmf.bukkit.models.User
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import org.bukkit.entity.Player

interface UserManager {
    val users: List<User>

    fun get(player: Player): User?

    fun create(player: Player, menu: BaseMenu, handler: PacketInterceptor.PacketInterceptorHandler): User

    fun remove(player: Player): User?
}
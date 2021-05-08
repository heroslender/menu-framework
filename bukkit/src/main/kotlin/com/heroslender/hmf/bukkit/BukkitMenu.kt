package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import com.heroslender.hmf.bukkit.utils.BoundingBox
import com.heroslender.hmf.core.Menu
import org.bukkit.entity.Player

interface BukkitMenu : Menu {
    val boundingBox: BoundingBox

    fun tickCursor(player: Player, x: Int, y: Int)

    fun onInteract(player: Player, action: PacketInterceptor.Action, x: Int, y: Int)
}
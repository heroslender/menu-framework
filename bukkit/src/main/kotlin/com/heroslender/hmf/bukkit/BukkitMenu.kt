package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import com.heroslender.hmf.bukkit.utils.BoundingBox
import com.heroslender.hmf.core.Menu

interface BukkitMenu: Menu {
    val boundingBox: BoundingBox

    fun tickCursor()

    fun onInteract(action: PacketInterceptor.Action): Boolean
}
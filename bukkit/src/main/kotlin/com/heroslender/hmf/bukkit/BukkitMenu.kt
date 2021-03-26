package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.utils.BoundingBox
import com.heroslender.hmf.core.Menu
import org.bukkit.event.block.Action

interface BukkitMenu: Menu {
    val boundingBox: BoundingBox

    fun tickCursor()

    fun onInteract(action: Action): Boolean
}
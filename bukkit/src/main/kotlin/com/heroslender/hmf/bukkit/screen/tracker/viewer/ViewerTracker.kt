package com.heroslender.hmf.bukkit.screen.tracker.viewer

import com.heroslender.hmf.bukkit.screen.tracker.Tracker
import org.bukkit.entity.Player

interface ViewerTracker: Tracker {
    val viewers: Array<Player>

    fun canCursor(player: Player): Boolean

    fun canInteract(player: Player): Boolean

    fun tick()
}
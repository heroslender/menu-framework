package com.heroslender.hmf.bukkit.screen.tracker.cursor

import org.bukkit.entity.Player

class StaticCursorTracker(private val allowance: Boolean = true) : CursorTracker {
    override fun isTracked(player: Player): Boolean = allowance
}

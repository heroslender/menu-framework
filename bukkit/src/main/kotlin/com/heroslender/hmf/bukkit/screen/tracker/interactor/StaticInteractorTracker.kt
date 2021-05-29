package com.heroslender.hmf.bukkit.screen.tracker.interactor

import org.bukkit.entity.Player

class StaticInteractorTracker(private val allowance: Boolean = true) : InteractorTracker {
    override fun isTracked(player: Player): Boolean = allowance
}

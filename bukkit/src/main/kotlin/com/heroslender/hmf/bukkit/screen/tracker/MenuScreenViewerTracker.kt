package com.heroslender.hmf.bukkit.screen.tracker

import org.bukkit.entity.Player

interface MenuScreenViewerTracker {
    val viewers: Array<Player>

    fun tick()
}
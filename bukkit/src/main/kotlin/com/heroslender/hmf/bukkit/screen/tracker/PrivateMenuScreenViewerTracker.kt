package com.heroslender.hmf.bukkit.screen.tracker

import org.bukkit.entity.Player

class PrivateMenuScreenViewerTracker(
    owner: Player,
) : MenuScreenViewerTracker {
    override val viewers: Array<Player> = arrayOf(owner)

    override fun tick() {}
}
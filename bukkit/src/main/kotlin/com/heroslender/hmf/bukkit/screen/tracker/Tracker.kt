package com.heroslender.hmf.bukkit.screen.tracker

import org.bukkit.entity.Player

interface Tracker {

    fun isTracked(player: Player): Boolean
}
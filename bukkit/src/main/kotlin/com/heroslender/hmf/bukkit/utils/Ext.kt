package com.heroslender.hmf.bukkit.utils

import com.heroslender.hmf.bukkit.BaseMenu
import org.bukkit.Location

/**
 * Ignores the return of a function
 */
inline fun Any?.ignore(): Unit {}

/**
 * The center location of the menu
 */
val BaseMenu.centerLocation: Location
    get() {
        val left = direction.rotateLeft()

        return Location(
            location.world,
            startX + width / 2.0 * left.x,
            startY - height / 2.0,
            startZ + width / 2.0 * left.z,
        )
    }
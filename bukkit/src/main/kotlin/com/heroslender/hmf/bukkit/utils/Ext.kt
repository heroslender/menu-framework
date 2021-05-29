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
        val opts = opts
        val left = opts.direction.rotateLeft()

        return opts.location.clone().add(
            opts.width / 2.0 * left.x,
            opts.height / 2.0,
            opts.width / 2.0 * left.z,
        )
    }
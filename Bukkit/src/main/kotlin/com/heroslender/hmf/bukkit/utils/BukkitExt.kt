package com.heroslender.hmf.bukkit.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin

/**
 * Fixes the yaw angle.
 *
 * By default yaw ranges from -360 to 360, applying this
 * it will fixed to range from 0 to 360
 */
fun patchYaw(a: Float): Float {
    return if (a <= 0.0f) 360.0f + a else a
}

/**
 * Fixes the yaw angle.
 *
 * By default yaw ranges from -360 to 360, applying this
 * it will fixed to range from 0 to 360
 */
val Location.patchedYaw: Float
    get() = patchYaw(yaw)

/**
 * Simplify scheduling bukkit tasks.
 */
inline fun scheduleAsyncTimer(plugin: Plugin, delay: Long, op: Runnable): Int =
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, op, 0, delay).taskId

package com.heroslender.hmf.bukkit.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin

/**
 * Simplify scheduling bukkit tasks.
 */
inline fun scheduleAsyncTimer(plugin: Plugin, delay: Long, op: Runnable): Int =
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, op, 0, delay).taskId

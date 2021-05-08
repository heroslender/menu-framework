package com.heroslender.hmf.bukkit.manager

import com.heroslender.hmf.bukkit.BaseMenu
import com.heroslender.hmf.core.MenuManager
import org.bukkit.entity.Player

interface BukkitMenuManager : MenuManager<Player, BaseMenu> {
    /**
     * Returns the next available entity id to be used
     * by maps & item frames.
     */
    fun nextEntityId(): Int

    /**
     * Executes [factory] while holding a lock on the entity ID provider.
     */
    fun <R> withEntityIdFactory(factory: (nextEntityId: () -> Int) -> R): R
}
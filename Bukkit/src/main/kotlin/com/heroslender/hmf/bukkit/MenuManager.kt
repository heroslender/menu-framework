package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.listeners.MenuListeners
import org.bukkit.entity.Player

class MenuManager {
    private val menus: MutableList<BaseMenu> = mutableListOf()

    fun getOwned(player: Player): BaseMenu? {
        return menus.firstOrNull { it.owner === player }
    }

    fun remove(player: Player) {
        menus.removeIf { !it.owner.isOnline || it.owner === player }
    }

    fun add(menu: BaseMenu) {
        MenuListeners.ensureRegistered()

        menus.add(menu)
    }
}
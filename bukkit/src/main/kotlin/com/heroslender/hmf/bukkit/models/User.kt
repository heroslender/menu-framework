package com.heroslender.hmf.bukkit.models

import com.heroslender.hmf.bukkit.BaseMenu
import org.bukkit.entity.Player

class User(
    val player: Player,
    val menu: BaseMenu,
) {
    fun tryInteract(): Boolean {
        val now = System.currentTimeMillis()
        if (nextInteraction > now) {
            return false
        }

        nextInteraction = now + INTERACT_COOLDOWN
        return true
    }

    private var nextInteraction: Long = System.currentTimeMillis()

    companion object {
        const val INTERACT_COOLDOWN = 200
    }
}

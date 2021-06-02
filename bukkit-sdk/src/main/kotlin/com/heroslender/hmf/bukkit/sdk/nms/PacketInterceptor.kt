package com.heroslender.hmf.bukkit.sdk.nms

import org.bukkit.entity.Player

interface PacketInterceptor {
    val player: Player
    val handler: PacketInterceptorHandler

    interface PacketInterceptorHandler {
        val handlerId: String

        fun handleInteraction(player: Player, entityId: Int, action: Action): Boolean
    }

    enum class Action {
        RIGHT_CLICK,
        LEFT_CLICK,
    }
}
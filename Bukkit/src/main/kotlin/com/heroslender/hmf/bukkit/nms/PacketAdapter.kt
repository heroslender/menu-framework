package com.heroslender.hmf.bukkit.nms

import org.bukkit.entity.Player

interface PacketAdapter {
    fun sendMapPacket(player: Player)
}
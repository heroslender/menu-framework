package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.nms.PacketAdapter
import com.heroslender.hmf.bukkit.nms.version.ServerVersion

object HmfBukkit {
    val packetAdapter: PacketAdapter = when (ServerVersion.CURRENT) {
        ServerVersion.V1_8_R3 -> com.heroslender.hmf.bukkit.nms.v1_8.PacketAdapterImpl()
        ServerVersion.V1_12_R1 -> com.heroslender.hmf.bukkit.nms.v1_12.PacketAdapterImpl()
    }

    val manager: MenuManager = MenuManager()
}
package com.heroslender.hmf.bukkit.sdk.nms

import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import com.heroslender.hmf.bukkit.sdk.nms.version.ServerVersion
import org.bukkit.entity.Player

interface PacketAdapter {
    fun spawnMapItemFrame(
        itemFrameID: Int,
        mapID: Int,
        x: Int,
        y: Int,
        z: Int,
        direction: Direction,
        players: Array<Player>,
    )

    fun updateMap(
        mapId: Int,
        scale: Byte,
        icons: Collection<MapIcon?>,
        data: ByteArray,
        offsetX: Int,
        offsetY: Int,
        sizeX: Int,
        sizeZ: Int,
        players: Array<Player>,
    )

    fun destroy(
        itemFrameID: Int,
        players: Array<Player>,
    )

    fun addPacketInterceptor(player: Player, handler: PacketInterceptor.PacketInterceptorHandler)

    companion object {
        @JvmStatic
        fun current(): PacketAdapter = when (ServerVersion.CURRENT) {
            ServerVersion.V1_8_R3 -> com.heroslender.hmf.bukkit.sdk.nms.v1_8.PacketAdapterImpl()
            ServerVersion.V1_12_R1 -> com.heroslender.hmf.bukkit.sdk.nms.v1_12.PacketAdapterImpl()
        }
    }
}
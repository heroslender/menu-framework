package com.heroslender.hmf.bukkit.nms

import com.heroslender.hmf.bukkit.Direction
import com.heroslender.hmf.bukkit.map.MapIcon
import org.bukkit.entity.Player

interface PacketAdapter {
    fun spawnMapItemFrame(
        itemFrameID: Int,
        mapID: Int,
        x: Int,
        y: Int,
        z: Int,
        direction: Direction,
        vararg players: Player,
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
        vararg players: Player,
    )

    fun destroy(
        itemFrameID: Int,
        vararg players: Player,
    )
}
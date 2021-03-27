package com.heroslender.hmf.bukkit.sdk.nms.v1_8

import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import com.heroslender.hmf.bukkit.sdk.nms.PacketAdapter
import net.minecraft.server.v1_8_R3.DataWatcher
import net.minecraft.server.v1_8_R3.Item
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Packet
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_8_R3.PacketPlayOutMap
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers
import org.bukkit.entity.Player

class PacketAdapterImpl : PacketAdapter {
    companion object {
        private val MAP_ITEM: Item = CraftMagicNumbers.getItem(Material.MAP)
    }

    override fun spawnMapItemFrame(
        itemFrameID: Int,
        mapID: Int,
        x: Int,
        y: Int,
        z: Int,
        direction: Direction,
        vararg players: Player,
    ) {
        val spawnPacket = PacketPlayOutSpawnEntity().apply {
            // ID
            setValue("a", itemFrameID)
            //entityTypeId
            setValue("j", 71)
            // x location
            setValue("b", x * 32)
            // y location
            setValue("c", y * 32)
            // z location
            setValue("d", z * 32)

            // Yaw : Int = `direction * 90 * 256.0F / 360.0F`
            setValue("i", direction.packetYaw)
            // Rotation/direction?: Int
            setValue("k", direction.rotation)
        }

        // Place map on item frame
        val metadata = PacketPlayOutEntityMetadata(
            itemFrameID,
            DataWatcher(null).apply {
                a(8, ItemStack(MAP_ITEM, 1, mapID))
            },
            true
        )

        for (player in players) {
            val playerConnection = (player as CraftPlayer).handle.playerConnection
            playerConnection.sendPacket(spawnPacket)
            playerConnection.sendPacket(metadata)
        }
    }

    override fun updateMap(
        mapId: Int,
        scale: Byte,
        icons: Collection<MapIcon?>,
        data: ByteArray,
        offsetX: Int,
        offsetY: Int,
        sizeX: Int,
        sizeZ: Int,
        vararg players: Player,
    ) {
        val cursors = icons.mapNotNull {
            it?.let { net.minecraft.server.v1_8_R3.MapIcon(it.type.typeId, it.x, it.y, it.direction) }
        }

        val mapPacket = PacketPlayOutMap(mapId, scale, cursors, data, offsetX, offsetY, sizeX, sizeZ)


        for (player in players) {
            (player as CraftPlayer).handle.playerConnection.sendPacket(mapPacket)
        }
    }

    override fun destroy(itemFrameID: Int, vararg players: Player) {
        val destroyPacket = PacketPlayOutEntityDestroy(itemFrameID)
        for (player in players) {
            (player as CraftPlayer).handle.playerConnection.sendPacket(destroyPacket)
        }
    }

    private fun Packet<*>.setValue(field: String, value: Any) {
        val clazz = this::class.java
        val packetField = clazz.getDeclaredField(field)

        packetField.isAccessible = true
        packetField.set(this, value)
    }
}
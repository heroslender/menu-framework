package com.heroslender.hmf.bukkit.nms.v1_12

import com.heroslender.hmf.bukkit.Direction
import com.heroslender.hmf.bukkit.map.MapIcon
import com.heroslender.hmf.bukkit.nms.PacketAdapter
import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.DataWatcherObject
import net.minecraft.server.v1_12_R1.DataWatcherRegistry
import net.minecraft.server.v1_12_R1.EntityItemFrame
import net.minecraft.server.v1_12_R1.Item
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers
import org.bukkit.entity.Player
import java.util.*

class PacketAdapterImpl : PacketAdapter {
    companion object {
        private val MAP_ITEM: Item = CraftMagicNumbers.getItem(Material.MAP)
        private val itemFrameItemWatcher: DataWatcherObject<ItemStack>? = DataWatcher.a(
            EntityItemFrame::class.java, DataWatcherRegistry.f
        )
    }

    override fun spawnMapItemFrame(
        itemFrameID: Int,
        mapID: Int,
        x: Int,
        y: Int,
        z: Int,
        direction: Direction,
        vararg players: Player
    ) {
        val spawnPacket = PacketPlayOutSpawnEntity().apply {
            // ID
            setValue("a", itemFrameID)
            // UUID
            setValue("b", UUID.randomUUID())
            //entityTypeId
            setValue("k", 71)
            // x location: Double
            setValue("c", x)
            // y location: Double
            setValue("d", y)
            // z location: Double
            setValue("e", z)

            // Yaw : Int = `direction * 90 * 256.0F / 360.0F`
            setValue("j", direction.packetYaw)
            // Rotation/direction?: Int
            setValue("l", direction.rotation)
        }

        // Place map on item frame
        val metadata = PacketPlayOutEntityMetadata(
            itemFrameID,
            DataWatcher(null).apply {
                register(itemFrameItemWatcher, ItemStack(MAP_ITEM, 1, mapID))
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
        TODO("To be implemented!")
    }

    private fun Packet<*>.setValue(field: String, value: Any) {
        val clazz = this::class.java
        val packetField = clazz.getDeclaredField(field)

        packetField.isAccessible = true
        packetField.set(this, value)
    }
}


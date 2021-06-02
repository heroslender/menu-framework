package com.heroslender.hmf.bukkit.sdk.nms.v1_12

import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import com.heroslender.hmf.bukkit.sdk.nms.PacketAdapter
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.DataWatcherObject
import net.minecraft.server.v1_12_R1.DataWatcherRegistry
import net.minecraft.server.v1_12_R1.Item
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_12_R1.PacketPlayOutMap
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers
import org.bukkit.entity.Player
import java.util.*

class PacketAdapterImpl : PacketAdapter {
    companion object {
        private val MAP_ITEM: Item = CraftMagicNumbers.getItem(Material.MAP)

        private val itemFrameItemWatcher: DataWatcherObject<ItemStack> = DataWatcherRegistry.f.a(6)
    }

    override fun addPacketInterceptor(player: Player, handler: PacketInterceptor.PacketInterceptorHandler) {
        PacketInterceptorImpl(player, handler)
    }

    override fun removePacketInterceptor(player: Player, handler: PacketInterceptor.PacketInterceptorHandler) {
        PacketInterceptorImpl.dispose(player, handler.handlerId)
    }

    override fun spawnMapItemFrame(
        itemFrameID: Int,
        mapID: Int,
        x: Int,
        y: Int,
        z: Int,
        direction: Direction,
        players: Array<Player>,
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
        players: Array<Player>,
    ) {
        val cursors = icons.mapNotNull {
            it?.let { net.minecraft.server.v1_12_R1.MapIcon(it.type.toNMS(), it.x, it.y, it.direction) }
        }

        // 3rd param is Tracking Position - Specifies whether player and item frame icons are shown.
        val mapPacket = PacketPlayOutMap(mapId, scale, false, cursors, data, offsetX, offsetY, sizeX, sizeZ)

        for (player in players) {
            (player as CraftPlayer).handle.playerConnection.sendPacket(mapPacket)
        }
    }

    override fun destroy(itemFrameID: Int, players: Array<Player>) {
        val destroyPacket = PacketPlayOutEntityDestroy(itemFrameID)
        for (player in players) {
            (player as CraftPlayer).handle.playerConnection.sendPacket(destroyPacket)
        }
    }

    private fun MapIcon.Type.toNMS(): net.minecraft.server.v1_12_R1.MapIcon.Type = when (this) {
        MapIcon.Type.WHITE_POINTER -> net.minecraft.server.v1_12_R1.MapIcon.Type.PLAYER
        MapIcon.Type.GREEN_POINTER -> net.minecraft.server.v1_12_R1.MapIcon.Type.FRAME
        MapIcon.Type.RED_POINTER -> net.minecraft.server.v1_12_R1.MapIcon.Type.RED_MARKER
        MapIcon.Type.BLUE_POINTER -> net.minecraft.server.v1_12_R1.MapIcon.Type.BLUE_MARKER
        MapIcon.Type.WHITE_CROSS -> net.minecraft.server.v1_12_R1.MapIcon.Type.TARGET_X
        MapIcon.Type.RED_TRIANGLE -> net.minecraft.server.v1_12_R1.MapIcon.Type.TARGET_POINT
        MapIcon.Type.LARGE_WHITE_DOT -> net.minecraft.server.v1_12_R1.MapIcon.Type.PLAYER_OFF_MAP
        MapIcon.Type.WHITE_DOT -> net.minecraft.server.v1_12_R1.MapIcon.Type.PLAYER_OFF_LIMITS
        MapIcon.Type.WOODLAND_MANSION -> net.minecraft.server.v1_12_R1.MapIcon.Type.MANSION
        MapIcon.Type.OCEAN_MONUMENT -> net.minecraft.server.v1_12_R1.MapIcon.Type.MONUMENT
        else -> net.minecraft.server.v1_12_R1.MapIcon.Type.PLAYER
    }

    private fun Packet<*>.setValue(field: String, value: Any) {
        val clazz = this::class.java
        val packetField = clazz.getDeclaredField(field)

        packetField.isAccessible = true
        packetField.set(this, value)
    }
}


package com.heroslender.hmf.bukkit.nms.v1_8

import com.heroslender.hmf.bukkit.nms.PacketAdapter
import net.minecraft.server.v1_8_R3.DataWatcher
import net.minecraft.server.v1_8_R3.Item
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Packet
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers
import org.bukkit.entity.Player

class PacketAdapterImpl : PacketAdapter {
    companion object {
        private val MAP_ITEM: Item = CraftMagicNumbers.getItem(Material.MAP)
    }

    override fun sendMapPacket(player: Player) {
        val x = 0
        val y = 0
        val z = 0

        val spawnPacket = PacketPlayOutSpawnEntity().apply {
            // ID
            setValue("a", 9999)
            //entityTypeId
            setValue("j", 71)
            // x location
            setValue("b", x * 32)
            // y location
            setValue("c", y * 32)
            // z location
            setValue("d", z * 32)

            // Yaw : Int = `direction * 90 * 256.0F / 360.0F`
            setValue("i", 0)
            // Rotation/direction?: Int
            setValue("k", 0)
        }

        val metadata = PacketPlayOutEntityMetadata(
            9999,
            DataWatcher(null).apply {
                a(8, ItemStack(MAP_ITEM, 1, 9999))
            },
            true
        )

        val playerConnection = (player as CraftPlayer).handle.playerConnection
        playerConnection.sendPacket(spawnPacket)
        playerConnection.sendPacket(metadata)
    }

    private fun Packet<*>.setValue(field: String, value: Any) {
        val clazz = this::class.java
        val packetField = clazz.getDeclaredField(field)

        packetField.isAccessible = true
        packetField.set(this, value)
    }
}
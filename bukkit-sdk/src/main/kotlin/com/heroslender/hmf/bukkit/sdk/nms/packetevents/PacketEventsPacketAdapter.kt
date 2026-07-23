package com.heroslender.hmf.bukkit.sdk.nms.packetevents

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.nbt.NBTInt
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMapData
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import com.heroslender.hmf.bukkit.sdk.nms.PacketAdapter
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import org.bukkit.entity.Player
import java.util.*

class PacketEventsPacketAdapter : PacketAdapter {

    override fun spawnMapItemFrame(
        itemFrameID: Int,
        mapID: Int,
        x: Int,
        y: Int,
        z: Int,
        direction: Direction,
        players: Array<Player>,
    ) {
        val api = PacketEvents.getAPI()
        val serverVersion = api.serverManager.version

        for (player in players) {
            val user = api.playerManager.getUser(player) ?: continue

            // Spawn the item frame entity — PE handles x32/yaw encoding internally
            // data int: pre-1.17 uses 2D rotation (0=SOUTH,1=WEST,2=NORTH,3=EAST)
            //           1.17+ uses 3D Direction ordinal (2=NORTH,3=SOUTH,4=WEST,5=EAST)
            val data = if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
                direction.rotation3D
            } else {
                direction.rotation
            }
            // Glow item frames (1.17+) render at full brightness regardless of world light
            val entityType = if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
                EntityTypes.GLOW_ITEM_FRAME
            } else {
                EntityTypes.ITEM_FRAME
            }
            val spawnPacket = WrapperPlayServerSpawnEntity(
                itemFrameID,
                Optional.of(UUID.randomUUID()),
                entityType,
                Vector3d(x.toDouble(), y.toDouble(), z.toDouble()),
                0F,                       // pitch
                direction.yaw.toFloat(),  // yaw in degrees — PE encodes to byte internally
                0f,                       // headYaw (1.19+)
                data,                     // orientation — 2D vs 3D depending on version
                Optional.empty(),         // velocity
            )
            user.sendPacket(spawnPacket)

            // Place the filled map on the item frame via entity metadata
            val mapItem = createMapItem(mapID, serverVersion)
            val itemSlotIndex = itemFrameItemSlotIndex(serverVersion)
            // Index 0 = INVISIBLE flag (0x20), hides the frame/border — item inside stays visible
            val metadataPacket = WrapperPlayServerEntityMetadata(
                itemFrameID,
                listOf(
                    EntityData(0, EntityDataTypes.BYTE, 0x20.toByte()),
                    EntityData(itemSlotIndex, EntityDataTypes.ITEMSTACK, mapItem),
                ),
            )
            user.sendPacket(metadataPacket)
        }
    }

    private fun createMapItem(mapID: Int, serverVersion: ServerVersion): ItemStack {
        val builder = ItemStack.builder()
            .type(ItemTypes.FILLED_MAP)
            .amount(1)

        when {
            serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_5) -> {
                // 1.20.5+: map ID moved to item component "map_id"
                builder.component(ComponentTypes.MAP_ID, mapID)
            }
            serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13) -> {
                // 1.13-1.20.4: map ID lives in NBT "map" tag
                builder.nbt("map", NBTInt(mapID))
            }
            else -> {
                // Pre-1.13: map ID = item damage (legacyData)
                builder.legacyData(mapID)
            }
        }

        return builder.build()
    }

    /**
     * Returns the entity metadata index for the item frame's displayed item.
     * Index varies by server version:
     *  1.17+: DATA_DIRECTION added at 8, pushing ITEM to 9
     *  1.15-1.16: ITEM at 8
     *  1.13-1.14: ITEM at 7
     *  1.9-1.12: ITEM at 6
     *  1.8: ITEM at 8
     */
    private fun itemFrameItemSlotIndex(version: ServerVersion): Int = when {
        version.isNewerThanOrEquals(ServerVersion.V_1_17) -> 9
        version.isNewerThanOrEquals(ServerVersion.V_1_15) -> 8
        version.isNewerThanOrEquals(ServerVersion.V_1_13) -> 7
        version.isNewerThanOrEquals(ServerVersion.V_1_9)  -> 6
        else -> 8 // 1.8.x
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
        val api = PacketEvents.getAPI()
        val decorations = icons.mapNotNull { icon ->
            icon?.let { toPEMapDecoration(it) }
        }

        for (player in players) {
            val user = api.playerManager.getUser(player) ?: continue
            val mapPacket = WrapperPlayServerMapData(
                mapId,
                scale,
                false,         // trackingPosition
                false,         // locked
                decorations,   // decorations
                sizeX,         // columns
                sizeZ,         // rows
                offsetX,       // x
                offsetY,       // z
                data,          // data
            )
            user.sendPacket(mapPacket)
        }
    }

    override fun destroy(
        itemFrameID: Int,
        players: Array<Player>,
    ) {
        val api = PacketEvents.getAPI()
        for (player in players) {
            val user = api.playerManager.getUser(player) ?: continue
            user.sendPacket(WrapperPlayServerDestroyEntities(itemFrameID))
        }
    }

    override fun addPacketInterceptor(player: Player, handler: PacketInterceptor.PacketInterceptorHandler) {
        PacketEventsPacketInterceptor.getInstance().addHandler(player, handler)
    }

    override fun removePacketInterceptor(player: Player, handler: PacketInterceptor.PacketInterceptorHandler) {
        PacketEventsPacketInterceptor.getInstance().removeHandler(handler.handlerId)
    }

    private fun toPEMapDecoration(icon: MapIcon): WrapperPlayServerMapData.MapDecoration {
        val peType = icon.type.toPEMapDecorationType()
        return WrapperPlayServerMapData.MapDecoration(
            peType,
            icon.x,
            icon.y,
            icon.direction,
            null, // displayName — none for our use case
        )
    }

    private fun MapIcon.Type.toPEMapDecorationType(): com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationType {
        return when (this) {
            MapIcon.Type.WHITE_POINTER ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.PLAYER
            MapIcon.Type.GREEN_POINTER ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.FRAME
            MapIcon.Type.RED_POINTER ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.RED_MARKER
            MapIcon.Type.BLUE_POINTER ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.BLUE_MARKER
            MapIcon.Type.WHITE_CROSS ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.TARGET_X
            MapIcon.Type.RED_TRIANGLE ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.TARGET_POINT
            MapIcon.Type.LARGE_WHITE_DOT ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.PLAYER_OFF_MAP
            MapIcon.Type.WHITE_DOT ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.PLAYER_OFF_LIMITS
            MapIcon.Type.WOODLAND_MANSION ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.MANSION
            MapIcon.Type.OCEAN_MONUMENT ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.MONUMENT
            else ->
                com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes.PLAYER
        }
    }
}

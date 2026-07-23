package com.heroslender.hmf.bukkit.sdk.nms.packetevents

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.heroslender.hmf.bukkit.sdk.nms.PacketInterceptor
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

/**
 * Single-instance PacketEvents-based packet interceptor.
 *
 * Unlike the per-version ChannelDuplexHandler implementations that inject into
 * each player's Netty pipeline, this registers one global [PacketListener] with
 * the PacketEvents API. Handler routing is done via an internal map keyed by
 * [PacketInterceptor.PacketInterceptorHandler.handlerId].
 *
 * Uses [PacketListenerPriority.HIGH] so we can cancel the event before other
 * listeners process it.
 */
class PacketEventsPacketInterceptor private constructor() : PacketListener {

    private val handlers: ConcurrentHashMap<String, PacketInterceptor.PacketInterceptorHandler> =
        ConcurrentHashMap()

    init {
        PacketEvents.getAPI().eventManager.registerListener(this, PacketListenerPriority.HIGH)
    }

    @Suppress("UNUSED_PARAMETER")
    fun addHandler(player: Player, handler: PacketInterceptor.PacketInterceptorHandler) {
        handlers[handler.handlerId] = handler
    }

    fun removeHandler(handlerId: String) {
        handlers.remove(handlerId)
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.packetType != PacketType.Play.Client.INTERACT_ENTITY) return

        val wrapper = WrapperPlayClientInteractEntity(event)
        val entityId = wrapper.entityId
        val player = event.getPlayer<Player>() ?: return

        val action = when (wrapper.action) {
            WrapperPlayClientInteractEntity.InteractAction.ATTACK ->
                PacketInterceptor.Action.LEFT_CLICK

            else ->
                PacketInterceptor.Action.RIGHT_CLICK
        }

        // Try each registered handler; if any handles it, cancel the event
        for (handler in handlers.values) {
            val handled = handler.handleInteraction(player, entityId, action)
            if (handled) {
                event.isCancelled = true
                return
            }
        }
    }

    companion object {
        @Volatile
        private var instance: PacketEventsPacketInterceptor? = null

        @JvmStatic
        fun getInstance(): PacketEventsPacketInterceptor {
            return instance ?: synchronized(this) {
                instance ?: PacketEventsPacketInterceptor().also { instance = it }
            }
        }
    }
}

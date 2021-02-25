package com.heroslender.hmf.bukkit

import org.bukkit.Location
import org.bukkit.entity.Player

enum class Direction(val x: Int, val y: Int, val z: Int, val rotation: Int) {
    NORTH(0, 0, -1, 2),
    EAST(1, 0, 0, 3),
    SOUTH(0, 0, 1, 0),
    WEST(-1, 0, 0, 1);

    val yaw: Int = rotation * 90
    val packetYaw: Int = (yaw * 256.0F / 360.0F).toInt()

    fun opposite(): Direction {
        return when (this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }

    fun rotateRight(): Direction {
        return when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }

    fun rotateLeft(): Direction {
        return when (this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }
    }

    companion object {
        fun from(player: Player): Direction = from(player.location)

        fun from(location: Location): Direction = from(location.patchedYaw)

        fun from(yaw: Float): Direction {
            return when (yaw.toInt()) {
                in 315..360, in 0..45 -> SOUTH
                in 45..135 -> WEST
                in 135..225 -> NORTH
                in 225..315 -> EAST
                else -> NORTH
            }
        }
    }
}
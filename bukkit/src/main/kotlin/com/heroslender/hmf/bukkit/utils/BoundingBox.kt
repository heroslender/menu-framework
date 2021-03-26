package com.heroslender.hmf.bukkit.utils

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector
import kotlin.math.max
import kotlin.math.min

class BoundingBox(
    val minX: Double = 0.0,
    val minY: Double = 0.0,
    val minZ: Double = 0.0,
    val maxX: Double = 0.0,
    val maxY: Double = 0.0,
    val maxZ: Double = 0.0,
) {

    companion object {
        val EMPTY: BoundingBox = BoundingBox(.0, .0, .0, .0, .0, .0)

        fun ofBlocks(corner1: Location, corner2: Location): BoundingBox {
            return BoundingBox(
                min(corner1.x, corner2.x),
                min(corner1.y, corner2.y),
                min(corner1.z, corner2.z),
                max(corner1.x, corner2.x) + 1,
                max(corner1.y, corner2.y) + 1,
                max(corner1.z, corner2.z) + .05,
            )
        }

        fun of(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): BoundingBox {
            return BoundingBox(
                min(minX, maxX),
                min(minY, maxY),
                min(minZ, maxZ),
                max(minX, maxX),
                max(minY, maxY),
                max(minZ, maxZ),
            )
        }
    }

    fun toMinLocation(world: World?): Location {
        return Location(world, this.minX, this.minY, this.minZ)
    }

    fun toMaxLocation(world: World?): Location {
        return Location(world, this.maxX, this.maxY, this.maxZ)
    }

    /**
     * Calculates the intersection of this bounding box with the specified line
     * segment.
     *
     *
     * Intersections at edges and corners yield one of the affected block faces
     * as hit result, but it is not defined which of them.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @return the ray trace hit result, or `null` if there is no hit
     */
    fun rayTrace(start: Vector, direction: Vector, maxDistance: Double): Vector? {
        require(direction.lengthSquared() > 0) { "Direction's magnitude is 0!" }
        if (maxDistance < 0.0) return null

        // ray start:
        val startX = start.x
        val startY = start.y
        val startZ = start.z

        // ray direction:
        val dir: Vector = direction.clone().normalize()
        val dirX = dir.x
        val dirY = dir.y
        val dirZ = dir.z

        // saving a few divisions below:
        // Note: If one of the direction vector components is 0.0, these
        // divisions result in infinity. But this is not a problem.
        val divX = 1.0 / dirX
        val divY = 1.0 / dirY
        val divZ = 1.0 / dirZ
        var tMin: Double
        var tMax: Double
        var hitBlockFaceMin: BlockFace?
        var hitBlockFaceMax: BlockFace?

        // intersections with x planes:
        if (dirX >= 0.0) {
            tMin = (this.minX - startX) * divX
            tMax = (this.maxX - startX) * divX
            hitBlockFaceMin = BlockFace.WEST
            hitBlockFaceMax = BlockFace.EAST
        } else {
            tMin = (this.maxX - startX) * divX
            tMax = (this.minX - startX) * divX
            hitBlockFaceMin = BlockFace.EAST
            hitBlockFaceMax = BlockFace.WEST
        }

        // intersections with y planes:
        val tyMin: Double
        val tyMax: Double
        val hitBlockFaceYMin: BlockFace
        val hitBlockFaceYMax: BlockFace
        if (dirY >= 0.0) {
            tyMin = (this.minY - startY) * divY
            tyMax = (this.maxY - startY) * divY
            hitBlockFaceYMin = BlockFace.DOWN
            hitBlockFaceYMax = BlockFace.UP
        } else {
            tyMin = (this.maxY - startY) * divY
            tyMax = (this.minY - startY) * divY
            hitBlockFaceYMin = BlockFace.UP
            hitBlockFaceYMax = BlockFace.DOWN
        }
        if (tMin > tyMax || tMax < tyMin) {
            return null
        }
        if (tyMin > tMin) {
            tMin = tyMin
            hitBlockFaceMin = hitBlockFaceYMin
        }
        if (tyMax < tMax) {
            tMax = tyMax
            hitBlockFaceMax = hitBlockFaceYMax
        }

        // intersections with z planes:
        val tzMin: Double
        val tzMax: Double
        val hitBlockFaceZMin: BlockFace
        val hitBlockFaceZMax: BlockFace
        if (dirZ >= 0.0) {
            tzMin = (this.minZ - startZ) * divZ
            tzMax = (this.maxZ - startZ) * divZ
            hitBlockFaceZMin = BlockFace.NORTH
            hitBlockFaceZMax = BlockFace.SOUTH
        } else {
            tzMin = (this.maxZ - startZ) * divZ
            tzMax = (this.minZ - startZ) * divZ
            hitBlockFaceZMin = BlockFace.SOUTH
            hitBlockFaceZMax = BlockFace.NORTH
        }
        if (tMin > tzMax || tMax < tzMin) {
            return null
        }
        if (tzMin > tMin) {
            tMin = tzMin
            hitBlockFaceMin = hitBlockFaceZMin
        }
        if (tzMax < tMax) {
            tMax = tzMax
            hitBlockFaceMax = hitBlockFaceZMax
        }

        // intersections are behind the start:
        if (tMax < 0.0) return null
        // intersections are to far away:
        if (tMin > maxDistance) {
            return null
        }

        // find the closest intersection:
        val t: Double
        val hitBlockFace: BlockFace
        if (tMin < 0.0) {
            t = tMax
            hitBlockFace = hitBlockFaceMax
        } else {
            t = tMin
            hitBlockFace = hitBlockFaceMin
        }
        // reusing the newly created direction vector for the hit position:
        val hitPosition = dir.multiply(t).add(start)
        return hitPosition
    }
}
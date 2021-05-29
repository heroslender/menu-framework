package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.models.PrivateViewerTrackerOptions
import com.heroslender.hmf.bukkit.models.PublicViewerTrackerOptions
import com.heroslender.hmf.bukkit.models.ViewerTrackerOptions
import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.sdk.map.MapIcon
import org.bukkit.Location
import org.bukkit.entity.Player

data class MenuOptions(
    /** Start location of the menu */
    val location: Location,
    /** The direction the menu will be facing */
    val direction: Direction,
    /** Width of the menu, in game blocks. 1 block = 128 pixels */
    val width: Int = 4,
    /** Height of the menu, in game blocks. 1 block = 128 pixels */
    val height: Int = 3,
    val cursor: CursorOptions = CursorOptions(),
    /** Options for the viewer tracker */
    val viewerTracker: ViewerTrackerOptions,
) {

    data class CursorOptions(
        val offsetX: Int = 10,
        val offsetY: Int = 10,
        val iconType: MapIcon.Type = MapIcon.Type.GREEN_POINTER,
        val iconRotation: Byte = 6,
    )

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder(
        /** Start location of the menu */
        var location: Location? = null,
        /** The direction the menu will be facing */
        var direction: Direction? = null,
        /** Width of the menu, in game blocks. 1 block = 128 pixels */
        var width: Int = 4,
        /** Height of the menu, in game blocks. 1 block = 128 pixels */
        var height: Int = 3,
        var cursor: CursorOptions = CursorOptions(),
        /** Options for the viewer tracker */
        var viewerTracker: ViewerTrackerOptions? = null,
    ) {
        fun build(): MenuOptions {
            val viewerTracker = this.viewerTracker
            require(viewerTracker != null) { "viewerTracker is null" }

            val direction = when {
                direction != null -> direction!!
                viewerTracker is PrivateViewerTrackerOptions -> Direction.from(viewerTracker.owner).opposite()
                else -> Direction.SOUTH
            }

            val location = when {
                location != null -> location!!
                viewerTracker is PublicViewerTrackerOptions -> {
                    val left = direction.rotateLeft()
                    val xOffset: Double = (-(width / 2)).toDouble()
                    val yOffset: Double = (-(height / 2)).toDouble()
                    viewerTracker.center.add(xOffset * left.x, yOffset, xOffset * left.z)
                }
                viewerTracker is PrivateViewerTrackerOptions -> {
                    val startScreen: Location = viewerTracker.owner.location.clone()
                        .apply { pitch = 0F }
                        .let { it.add(it.direction.multiply(2)) }

                    val left = direction.rotateLeft()
                    val startOffset = -(width / 2 - 1).toDouble()
                    startScreen.add(startOffset * left.x, -1.0, startOffset * left.z)
                }
                else -> {
                    error("location wasn't specified")
                }
            }

            return MenuOptions(
                location = location,
                direction = direction,
                width = this.width,
                height = this.height,
                cursor = this.cursor,
                viewerTracker = viewerTracker,
            )
        }
    }

    companion object {
        inline fun builder(op: MenuOptions.Builder.() -> Unit): MenuOptions =
            MenuOptions.Builder().apply(op).build()
    }
}


infix fun MenuOptions.Builder.height(newWidth: Int): MenuOptions.Builder = apply {
    height = newWidth
}

infix fun MenuOptions.Builder.width(newWidth: Int): MenuOptions.Builder = apply {
    width = newWidth
}

infix fun MenuOptions.Builder.privateFor(owner: Player): MenuOptions.Builder = privateViewerTracker(owner)

infix fun MenuOptions.Builder.privateViewerTracker(owner: Player): MenuOptions.Builder = apply {
    viewerTracker = PrivateViewerTrackerOptions(owner)
}

fun MenuOptions.Builder.publicViewerTracker(
    center: Location,
    range: Int = 50,
    lifetime: Int = 1000,
): MenuOptions.Builder = apply {
    viewerTracker = PublicViewerTrackerOptions(center, range, lifetime)
}

fun MenuOptions.Builder.publicViewerTracker(
    player: Player,
    range: Int = 50,
    lifetime: Int = 1000,
): MenuOptions.Builder = apply {
    viewerTracker = PublicViewerTrackerOptions(
        center = player.location.clone()
            .apply { pitch = 0F }
            .let { it.add(it.direction.multiply(2)) },
        range = range,
        lifetime = lifetime,
    )
}

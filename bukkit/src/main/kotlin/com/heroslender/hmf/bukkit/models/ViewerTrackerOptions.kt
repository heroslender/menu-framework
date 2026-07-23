package com.heroslender.hmf.bukkit.models

import com.heroslender.hmf.bukkit.screen.tracker.cursor.StaticCursorTracker
import com.heroslender.hmf.bukkit.screen.tracker.interactor.StaticInteractorTracker
import com.heroslender.hmf.bukkit.screen.tracker.viewer.NearbyViewerTracker
import com.heroslender.hmf.bukkit.screen.tracker.viewer.PrivateViewerTracker
import com.heroslender.hmf.bukkit.screen.tracker.viewer.ViewerTracker
import org.bukkit.Location
import org.bukkit.entity.Player

interface ViewerTrackerOptions {

    fun make(): ViewerTracker
}

data class PrivateViewerTrackerOptions(

    /**
     * The sole viewer of the menu.
     */
    val owner: Player,
    val allowCursor: Boolean = true,
    val allowInteraction: Boolean = true,
) : ViewerTrackerOptions {

    override fun make(): ViewerTracker = PrivateViewerTracker(
        owner,
        StaticCursorTracker(allowCursor),
        StaticInteractorTracker(allowInteraction),
    )
}

data class PublicViewerTrackerOptions(

    /**
     * Center location to get the players nearby.
     */
    val center: Location,

    /**
     * The range to check for nearby players.
     */
    val range: Int,

    /**
     * For how long each check gets cached. This is in milliseconds.
     */
    val lifetime: Int,
    val allowCursor: Boolean = true,
    val allowInteraction: Boolean = true,
) : ViewerTrackerOptions {

    override fun make(): ViewerTracker = NearbyViewerTracker(
        center, range, lifetime,
        StaticCursorTracker(allowCursor),
        StaticInteractorTracker(allowInteraction),
    )
}
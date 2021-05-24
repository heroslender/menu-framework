package com.heroslender.hmf.bukkit.models

import com.heroslender.hmf.bukkit.screen.tracker.MenuScreenViewerTracker
import com.heroslender.hmf.bukkit.screen.tracker.NearbyMenuScreenViewerTracker
import com.heroslender.hmf.bukkit.screen.tracker.PrivateMenuScreenViewerTracker
import org.bukkit.Location
import org.bukkit.entity.Player

interface ViewerTrackerOptions {

    fun make(): MenuScreenViewerTracker
}

data class PrivateViewerTrackerOptions(

    /**
     * The sole viewer of the menu.
     */
    val owner: Player,
) : ViewerTrackerOptions {

    override fun make(): MenuScreenViewerTracker = PrivateMenuScreenViewerTracker(owner)
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
) : ViewerTrackerOptions {

    override fun make(): MenuScreenViewerTracker = NearbyMenuScreenViewerTracker(center, range, lifetime)
}
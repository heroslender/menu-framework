package com.heroslender.hmf.bukkit.screen.tracker.viewer

import com.heroslender.hmf.bukkit.screen.tracker.cursor.CursorTracker
import com.heroslender.hmf.bukkit.screen.tracker.interactor.InteractorTracker
import org.bukkit.Location
import org.bukkit.entity.Player

class NearbyViewerTracker(
    private val location: Location,
    range: Int,
    private val lifetime: Int,
    cursorTracker: CursorTracker,
    interactorTracker: InteractorTracker,
) : AbstractViewerTracker(cursorTracker, interactorTracker) {
    private val rangeSquared: Int = range * range

    private var expiration: Long = 0
    override var viewers: Array<Player> = emptyArray()
        private set

    private fun computeViewers(currentViewers: Array<Player>) {
        val newViewers = mutableListOf<Player>()
        for (player in location.world.players) {
            if (player.location.distanceSquared(location) <= rangeSquared) {
                newViewers.add(player)
            }
        }

        // Players to remove
        currentViewers.filter { !newViewers.contains(it) }

        // Players to add
        newViewers.filter { !currentViewers.contains(it) }

        viewers = newViewers.toTypedArray()
    }

    override fun tick() {
        if (System.currentTimeMillis() > expiration) {
            computeViewers(viewers)
            expiration = System.currentTimeMillis() + lifetime
        }
    }
}
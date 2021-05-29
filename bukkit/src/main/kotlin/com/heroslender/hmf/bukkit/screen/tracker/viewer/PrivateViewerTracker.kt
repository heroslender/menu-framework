package com.heroslender.hmf.bukkit.screen.tracker.viewer

import com.heroslender.hmf.bukkit.screen.tracker.cursor.CursorTracker
import com.heroslender.hmf.bukkit.screen.tracker.interactor.InteractorTracker
import org.bukkit.entity.Player

class PrivateViewerTracker(
    owner: Player,
    cursorTracker: CursorTracker,
    interactorTracker: InteractorTracker,
) : AbstractViewerTracker(cursorTracker, interactorTracker) {
    override val viewers: Array<Player> = arrayOf(owner)

    override fun isTracked(player: Player): Boolean = viewers[0] == player

    override fun tick() {}
}
package com.heroslender.hmf.bukkit.screen.tracker.viewer

import com.heroslender.hmf.bukkit.screen.tracker.cursor.CursorTracker
import com.heroslender.hmf.bukkit.screen.tracker.interactor.InteractorTracker
import org.bukkit.entity.Player

abstract class AbstractViewerTracker(
    private val cursorTracker: CursorTracker,
    private val interactorTracker: InteractorTracker,
) : ViewerTracker {

    override fun isTracked(player: Player): Boolean = viewers.contains(player)

    override fun canCursor(player: Player): Boolean = cursorTracker.isTracked(player)

    override fun canInteract(player: Player): Boolean = interactorTracker.isTracked(player)
}
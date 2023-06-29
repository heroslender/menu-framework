package com.heroslender.hmf.core.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ImageProvider
import com.heroslender.hmf.core.ui.LayoutNode
import com.heroslender.hmf.core.ui.modifier.Constraints
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

val LocalClickHandler: ProvidableCompositionLocal<ClickHandler> =
    staticCompositionLocalOf { error("No provider for local click handler") }
val LocalCanvas: ProvidableCompositionLocal<Canvas?> =
    staticCompositionLocalOf { null }
val LocalImageProvider: ProvidableCompositionLocal<ImageProvider> =
    staticCompositionLocalOf { error("No provider for local image provider") }

interface ClickHandler {
    fun <T> processClick(x: Int, y: Int, data: T)
}

class ComposeMenu : CoroutineScope {
    var hasFrameWaiters = false
    val clock = BroadcastFrameClock { hasFrameWaiters = true }
    val composeScope = CoroutineScope(Dispatchers.Default) + clock
    override val coroutineContext: CoroutineContext = composeScope.coroutineContext

    val rootNode = LayoutNode().apply { name = "root" }
    var updateHandler: () -> Unit = {}

    var running = false
    private val recomposer = Recomposer(coroutineContext)
    private val composition = Composition(MenuNodeApplier(rootNode), recomposer)

    var applyScheduled = false
    val snapshotHandle = Snapshot.registerGlobalWriteObserver {
        if (!applyScheduled) {
            applyScheduled = true
            composeScope.launch {
                applyScheduled = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    var exitScheduled = false

    fun exit() {
        exitScheduled = true
    }

    fun start(content: @Composable () -> Unit) {
        !running || return
        running = true

        MenuScopeManager.scopes += composeScope
        launch {
            recomposer.runRecomposeAndApplyChanges()
        }

        launch {
            hasFrameWaiters = true
            composition.setContent {
                CompositionLocalProvider(LocalClickHandler provides object : ClickHandler {
                    override fun <T> processClick(x: Int, y: Int, data: T) {
                        hasFrameWaiters = true
                        println("click")
                        rootNode.foldOut(false) { acc, component ->
                            if (!acc) {
                                if (component.checkIntersects(x, y)) {
                                    return@foldOut component.tryClick(x, y, data)
                                }
                            }

                            return@foldOut acc
                        }
                    }
                }) {
                    content()
                }
            }

            while (!exitScheduled) {
                //            Bukkit.getScheduler().scheduleSyncRepeatingTask(guiyPlugin, {
                if (hasFrameWaiters) {
                    hasFrameWaiters = false
                    clock.sendFrame(System.nanoTime()) // Frame time value is not used by Compose runtime.
                    rootNode.measure(Constraints())
                    rootNode.outerWrapper.placeAt(0, 0)
                    val draw = rootNode.draw(null)
                    if (draw) {
                        updateHandler()
                    }
                }
                delay(50)
            }
            running = false
            recomposer.close()
            snapshotHandle.dispose()
            composition.dispose()
            composeScope.cancel()
        }
    }
}

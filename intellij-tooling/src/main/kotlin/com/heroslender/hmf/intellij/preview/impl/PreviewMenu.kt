package com.heroslender.hmf.intellij.preview.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.Menu
import com.heroslender.hmf.core.compose.LocalCanvas
import com.heroslender.hmf.core.compose.LocalImageProvider
import com.heroslender.hmf.core.compose.LocalMenu
import com.heroslender.hmf.core.ui.components.Box
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.maxSize
import com.heroslender.hmf.intellij.preview.invokePreview
import com.intellij.openapi.application.runReadAction
import org.jetbrains.kotlin.psi.KtNamedFunction

class PreviewMenu(private val function: KtNamedFunction) : Menu {
    var name: String = "Preview"
        private set

    var canvas: Canvas = PreviewCanvas.EMPTY
        private set

    @Composable
    override fun getUi() {
        val (preview, composable) = runReadAction { invokePreview(function) }

        val manager = PreviewMenuManager(preview.javaClass.classLoader)
        canvas = PreviewCanvas(preview.width, preview.height)

        CompositionLocalProvider(
            LocalCanvas provides canvas,
            LocalImageProvider provides manager.imageProvider,
            LocalMenu provides this,
        ) {
            Box(modifier = Modifier.maxSize(canvas.width, canvas.height)) {
                composable()
            }
        }

        name = preview.name
        if (name.isEmpty()) {
            name = function.name ?: "Unknown"
        }
    }

    override fun close() {
    }
}
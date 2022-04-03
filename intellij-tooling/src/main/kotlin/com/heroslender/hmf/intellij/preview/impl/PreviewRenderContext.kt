package com.heroslender.hmf.intellij.preview.impl

import com.heroslender.hmf.core.*
import com.heroslender.hmf.core.ui.Composable

class PreviewRenderContext(
    override val canvas: PreviewCanvas,
    override var root: Composable? = null,
    classLoader: ClassLoader,
) : RenderContext {
    override val manager: PreviewMenuManager = PreviewMenuManager(classLoader)
    override lateinit var menu: Menu

    private var callback: () -> Unit = {}

    override fun update() {
        callback()
    }

    override fun onUpdate(callback: () -> Unit) {
        this.callback = callback
    }




}
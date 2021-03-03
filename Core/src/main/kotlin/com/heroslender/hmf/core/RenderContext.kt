package com.heroslender.hmf.core

interface RenderContext {
    val canvas: Canvas

    fun update()

    fun onUpdate(callback: () -> Unit)
}
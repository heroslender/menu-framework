package com.heroslender.hmf.core

interface Canvas {
    val width: Int
    val height: Int

    fun setPixel(x: Int, y: Int, color: IColor)
}
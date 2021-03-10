package com.heroslender.hmf.core

interface Canvas {
    val width: Int
    val height: Int

    fun setPixel(x: Int, y: Int, color: IColor)

    fun setPixelByte(x: Int, y: Int, color: Byte)

    fun getPixel(x: Int, y: Int): IColor

    fun getPixelByte(x: Int, y: Int): Byte

    fun draw(other: Canvas, offsetX: Int = 0, offsetY: Int = 0)

    fun newCanvas(width: Int = this.width, height: Int = this.height): Canvas
}
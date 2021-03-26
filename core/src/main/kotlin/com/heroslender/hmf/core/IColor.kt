package com.heroslender.hmf.core

import java.awt.Color

/**
 * A color to be used in the UI.
 */
interface IColor {
    /**
     * The ID of the color, for Minecraft maps.
     *
     * @see <a href="https://minecraft.gamepedia.com/Map_item_format#Color_table">Map Item Format - Color Table</a>
     */
    val id: Byte

    /**
     * The color value.
     */
    val color: Color

    val isTransparent: Boolean

    companion object {
        lateinit var TRANSPARENT: IColor
    }
}
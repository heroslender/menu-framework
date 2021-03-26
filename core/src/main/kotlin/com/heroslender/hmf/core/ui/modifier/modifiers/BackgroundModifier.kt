package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.drawer.Drawer
import com.heroslender.hmf.core.ui.modifier.drawer.drawer
import com.heroslender.hmf.core.ui.modifier.then

/**
 * Changes the component background to the given [color].
 *
 * If the color is transparent it is ignored.
 */
fun Modifier.backgroundColor(color: IColor): Modifier =
    if (color.isTransparent)
        this
    else
        this then backgroundColorDrawer(color)

/**
 * Instantiates a new [Drawer] that will change the component
 * background to the given [color].
 */
fun backgroundColorDrawer(color: IColor): Drawer = drawer { setPixel ->
    val width = this.width
    val height = this.height

    for (x in 0 until width) {
        for (y in 0 until height) {
            setPixel(x, y, color)
        }
    }
}
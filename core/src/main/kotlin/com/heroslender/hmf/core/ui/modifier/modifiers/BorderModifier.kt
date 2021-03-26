package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.drawer.BorderDrawer
import com.heroslender.hmf.core.ui.modifier.drawer.BorderRadius
import com.heroslender.hmf.core.ui.modifier.drawer.BorderRadiusDrawer
import com.heroslender.hmf.core.ui.modifier.then

fun Modifier.border(
    color: IColor,
    thickness: Int = 1,
    radius: Int = 0,
): Modifier = border(
    thickness = thickness,
    color = color,
    topLeft = radius,
    topRight = radius,
    bottomRight = radius,
    bottomLeft = radius,
)

fun Modifier.border(
    color: IColor,
    thickness: Int = 1,
    topLeft: Int = 0,
    topRight: Int = 0,
    bottomRight: Int = 0,
    bottomLeft: Int = 0,
): Modifier = border(
    thickness = thickness,
    color = color,
    topLeft = BorderRadiusDrawer.of(topLeft).topLeft,
    topRight = BorderRadiusDrawer.of(topRight).topRight,
    bottomRight = BorderRadiusDrawer.of(bottomRight).bottomRight,
    bottomLeft = BorderRadiusDrawer.of(bottomLeft).bottomLeft,
)

fun Modifier.border(
    color: IColor,
    thickness: Int,
    topLeft: BorderRadius,
    topRight: BorderRadius,
    bottomRight: BorderRadius,
    bottomLeft: BorderRadius,
): Modifier {
    if (color == IColor.TRANSPARENT ||
        (topLeft.radius == 0 && topRight.radius == 0 && bottomLeft.radius == 0 && bottomRight.radius == 0)
    ) {
        return this
    }

    return this then borderDrawer(
        thickness = thickness,
        color = color,
        topLeft = topLeft,
        topRight = topRight,
        bottomRight = bottomRight,
        bottomLeft = bottomLeft,
        inner = this.extra.any { it is BorderDrawer }
    )
}

fun borderDrawer(
    thickness: Int,
    color: IColor,
    radius: Int = 0,
    inner: Boolean = false,
): BorderDrawer = borderDrawer(
    thickness = thickness,
    color = color,
    topLeft = radius,
    topRight = radius,
    bottomRight = radius,
    bottomLeft = radius,
    inner = inner
)

fun borderDrawer(
    thickness: Int,
    color: IColor,
    topLeft: Int = 0,
    topRight: Int = 0,
    bottomRight: Int = 0,
    bottomLeft: Int = 0,
    inner: Boolean = false,
): BorderDrawer = borderDrawer(
    thickness = thickness,
    color = color,
    topLeft = BorderRadiusDrawer.of(topLeft).topLeft,
    topRight = BorderRadiusDrawer.of(topRight).topRight,
    bottomRight = BorderRadiusDrawer.of(bottomRight).bottomRight,
    bottomLeft = BorderRadiusDrawer.of(bottomLeft).bottomLeft,
    inner = inner
)

fun borderDrawer(
    thickness: Int,
    color: IColor,
    topLeft: BorderRadius,
    topRight: BorderRadius,
    bottomRight: BorderRadius,
    bottomLeft: BorderRadius,
    inner: Boolean = false,
): BorderDrawer = BorderDrawer(
    thickness = thickness,
    color = color,
    topLeft = topLeft,
    topRight = topRight,
    bottomRight = bottomRight,
    bottomLeft = bottomLeft,
    inner = inner
)
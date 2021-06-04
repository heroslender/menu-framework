package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.State
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.DrawerModifier
import com.heroslender.hmf.core.ui.withState

fun Composable.ProgressBar(
    progressState: State<Int>,
    filledColor: IColor,
    backgroundColor: IColor,
    modifier: Modifier = Modifier,
) {
    val mod = modifier.then(StatedProgressBarDrawer(progressState, filledColor, backgroundColor))

    appendComponent(mod).withState(progressState)
}

fun Composable.ProgressBar(
    progress: Int,
    filledColor: IColor,
    backgroundColor: IColor,
    modifier: Modifier = Modifier,
) {
    val mod = modifier.then(ProgressBarDrawer(progress, filledColor, backgroundColor))

    appendComponent(mod)
}

class StatedProgressBarDrawer(
    private val progressState: State<Int>,
    filledColor: IColor,
    backgroundColor: IColor,
) : ProgressBarDrawer(0, filledColor, backgroundColor) {
    override val percentage: Int
        get() = progressState.value
}

open class ProgressBarDrawer(
    open val percentage: Int,
    private val filledColor: IColor,
    private val backgroundColor: IColor,
) : DrawerModifier {

    override fun Placeable.onDraw(canvas: Canvas) {
        val width = width
        val height = height
        val mark: Int = (width * (percentage / 100.0)).toInt()

        for (x in 0 until mark) {
            for (y in 0 until height) {
                canvas.setPixel(x, y, filledColor)
            }
        }
        for (x in mark until width) {
            for (y in 0 until height) {
                canvas.setPixel(x, y, backgroundColor)
            }
        }
    }
}

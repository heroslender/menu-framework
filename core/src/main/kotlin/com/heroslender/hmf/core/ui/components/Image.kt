@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.layout
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.DrawerModifier
import kotlin.math.min

interface Image {
    val width: Int

    val height: Int

    fun Placeable.draw(canvas: Canvas)
}

inline fun Composable.Image(
    asset: String,
    width: Int = -1,
    height: Int = -1,
    cached: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val image = renderContext.manager.getImage(asset, width, height, cached) ?: return

    Image(
        image = image,
        modifier = modifier,
    )
}

inline fun Composable.Image(
    image: Image,
    modifier: Modifier = Modifier,
) {
    val mod = modifier.then(ImageDrawer(image))

    appendComponent(mod) {
        measurableGroup = newMeasurableGroup { _, constraints ->
            val width = min(image.width, constraints.maxWidth)
            val height = min(image.height, constraints.maxHeight)

            layout(width, height)
        }
    }
}

class ImageDrawer(
    val image: Image,
) : DrawerModifier {
    override fun Placeable.onDraw(canvas: Canvas) {
        with(image) {
            draw(canvas)
        }
    }
}

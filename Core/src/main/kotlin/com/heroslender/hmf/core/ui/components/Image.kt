@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.DrawableComponent
import com.heroslender.hmf.core.ui.modifier.Modifier

interface Image {
    val width: Int

    val height: Int

    fun draw(canvas: Canvas, offsetX: Int = 0, offsetY: Int = 0)
}

inline fun Composable.Image(
    asset: String,
    cached: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val image = renderContext.manager.getImage(asset, cached)?: return

    Image(
        image = image,
        modifier = modifier,
    )
}

inline fun Composable.Image(
    image: Image,
    modifier: Modifier = Modifier,
) {
    val component = ImageComponent(
        image = image,
        modifier = modifier,
        parent = this
    )
    addChild(component)
}

class ImageComponent(
    val image: Image,
    modifier: Modifier,
    parent: Composable,
) : DrawableComponent(parent, modifier) {
    override val contentHeight: Int = image.height
    override val contentWidth: Int = image.width

    override fun draw(canvas: Canvas) {
        image.draw(canvas, modifier.paddingLeft, modifier.paddingTop)
    }
}
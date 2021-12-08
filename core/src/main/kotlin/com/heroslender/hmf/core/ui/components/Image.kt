@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ImageProvider
import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
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
    resizeMode: ImageProvider.ImageResizeMode = ImageProvider.ImageResizeMode.CONTAIN,
    cached: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val imageAssetDrawer = ImageAssetDrawer(
        imageProvider = renderContext.manager.imageProvider,
        asset = asset,
        resizeMode = resizeMode,
        cached = cached,
    )

    appendComponent(modifier.then(imageAssetDrawer)) {
        measurableGroup = imageAssetDrawer
    }
}

class ImageAssetDrawer(
    private val imageProvider: ImageProvider,
    private val asset: String,
    private val resizeMode: ImageProvider.ImageResizeMode,
    private val cached: Boolean,
) : DrawerModifier, MeasurableGroup {
    private var image: Image? = null

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult {
        if (constraints.maxHeight == Constraints.Infinity && constraints.maxWidth == Constraints.Infinity) {
            return layout(constraints.minWidth, constraints.minHeight)
        }

        val image = imageProvider.getImage(
            url = asset,
            width = if (constraints.maxWidth == Constraints.Infinity) -1 else constraints.maxWidth,
            height = if (constraints.maxHeight == Constraints.Infinity) -1 else constraints.maxHeight,
            resizeMode = resizeMode,
            cached = cached
        ) ?: return layout(constraints.minWidth, constraints.minHeight)

        this@ImageAssetDrawer.image = image
        return layout(image.width, image.height)
    }

    override fun Placeable.onDraw(canvas: Canvas) {
        with(image ?: return) {
            draw(canvas)
        }
    }
}

@Deprecated("Use Image(asset, width, height, resizeMode, cached, modifier)")
inline fun Composable.Image(
    asset: String,
    width: Int = -1,
    height: Int = -1,
    cached: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val image = renderContext.manager.imageProvider.getImage(asset,
        width,
        height,
        ImageProvider.ImageResizeMode.STRETCH,
        cached) ?: return

    Image(
        image = image,
        modifier = modifier,
    )
}

@Deprecated("Use Image(asset, width, height, resizeMode, cached, modifier)")
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

@Deprecated("Use ImageAssetDrawer")
class ImageDrawer(
    val image: Image,
) : DrawerModifier {
    override fun Placeable.onDraw(canvas: Canvas) {
        with(image) {
            draw(canvas)
        }
    }
}

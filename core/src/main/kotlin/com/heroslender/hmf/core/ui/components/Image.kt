@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package com.heroslender.hmf.core.ui.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.Canvas
import com.heroslender.hmf.core.ImageProvider
import com.heroslender.hmf.core.compose.Layout
import com.heroslender.hmf.core.compose.LocalImageProvider
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

@Composable
inline fun Image(
    asset: String,
    resizeMode: ImageProvider.ImageResizeMode = ImageProvider.ImageResizeMode.CONTAIN,
    cached: Boolean = true,
    modifier: Modifier = Modifier,
) = ImageAssetDrawer(
    imageProvider = LocalImageProvider.current,
    asset = asset,
    resizeMode = resizeMode,
    cached = cached,
).let { imageAssetDrawer ->
    Layout(
        measurableGroup = imageAssetDrawer,
        modifier = modifier.then(imageAssetDrawer),
    )
}

@Composable
inline fun Image(
    image: Image,
    modifier: Modifier = Modifier,
) = Layout(
    measurableGroup = newMeasurableGroup { _, constraints ->
        val width = min(image.width, constraints.maxWidth)
        val height = min(image.height, constraints.maxHeight)

        layout(width, height)
    },
    modifier = modifier.then(ImageDrawer(image))
)

class ImageDrawer(
    val image: Image,
) : DrawerModifier {
    override fun Placeable.onDraw(canvas: Canvas) {
        with(image) {
            draw(canvas)
        }
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

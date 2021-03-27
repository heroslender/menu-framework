package com.heroslender.hmf.core.ui.modifier

open class Modifier(
    val height: Size = FitContent,
    val width: Size = FitContent,

    val verticalAlignment: VerticalAlignment = VerticalAlignment.TOP,
    val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.START,

    val margin: MarginValues = MarginValues(),
    val padding: PaddingValues = PaddingValues(),

    val extra: ModifierExtra = ModifierExtra,
) {
    companion object : Modifier()

    fun copy(
        height: Size = this.height,
        width: Size = this.width,
        verticalAlignment: VerticalAlignment = this.verticalAlignment,
        horizontalAlignment: HorizontalAlignment = this.horizontalAlignment,
        margin: MarginValues = this.margin,
        padding: PaddingValues = this.padding,
        extra: ModifierExtra = this.extra,
    ): Modifier {
        return Modifier(
            height = height,
            width = width,
            verticalAlignment = verticalAlignment,
            horizontalAlignment = horizontalAlignment,
            margin = margin,
            padding = padding,
            extra = extra
        )
    }
}

enum class VerticalAlignment {
    TOP,
    CENTER,
    BOTTOM,
}

enum class HorizontalAlignment {
    START,
    CENTER,
    END,
}

/**
 * The size of a component.
 */
sealed class Size(val value: Int)

/**
 * Fix the component size at the given [value].
 */
class FixedSize(value: Int) : Size(value)

/**
 * The component size will adapt to fill the available space.
 */
object Fill : Size(1)

/**
 * The component size will adapt to fit the content.
 */
object FitContent : Size(1)

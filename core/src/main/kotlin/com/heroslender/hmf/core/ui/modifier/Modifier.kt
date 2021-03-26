package com.heroslender.hmf.core.ui.modifier

open class Modifier(
    val height: Size = FitContent,
    val width: Size = FitContent,

    val verticalAlignment: VerticalAlignment = VerticalAlignment.TOP,
    val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.START,

    val marginTop: Int = 0,
    val marginRight: Int = 0,
    val marginBottom: Int = 0,
    val marginLeft: Int = 0,

    val paddingTop: Int = 0,
    val paddingRight: Int = 0,
    val paddingBottom: Int = 0,
    val paddingLeft: Int = 0,

    val extra: ModifierExtra = ModifierExtra,
) {
    companion object : Modifier()

    fun copy(
        height: Size = this.height,
        width: Size = this.width,

        verticalAlignment: VerticalAlignment = this.verticalAlignment,
        horizontalAlignment: HorizontalAlignment = this.horizontalAlignment,

        marginTop: Int = this.marginTop,
        marginRight: Int = this.marginRight,
        marginBottom: Int = this.marginBottom,
        marginLeft: Int = this.marginLeft,

        paddingTop: Int = this.paddingTop,
        paddingRight: Int = this.paddingRight,
        paddingBottom: Int = this.paddingBottom,
        paddingLeft: Int = this.paddingLeft,

        extra: ModifierExtra = this.extra,
    ): Modifier {
        return Modifier(
            height = height,
            width = width,
            verticalAlignment = verticalAlignment,
            horizontalAlignment = horizontalAlignment,
            marginTop = marginTop,
            marginRight = marginRight,
            marginBottom = marginBottom,
            marginLeft = marginLeft,
            paddingTop = paddingTop,
            paddingRight = paddingRight,
            paddingBottom = paddingBottom,
            paddingLeft = paddingLeft,
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

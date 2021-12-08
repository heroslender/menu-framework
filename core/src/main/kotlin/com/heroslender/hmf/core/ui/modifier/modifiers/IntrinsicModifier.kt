package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.type.LayoutModifier

fun Modifier.width(intrinsicSize: IntrinsicSize) = when (intrinsicSize) {
    IntrinsicSize.Min -> this.then(MinIntrinsicWidthModifier)
    IntrinsicSize.Max -> this.then(MaxIntrinsicWidthModifier)
}

fun Modifier.height(intrinsicSize: IntrinsicSize) = when (intrinsicSize) {
    IntrinsicSize.Min -> this.then(MinIntrinsicHeightModifier)
    IntrinsicSize.Max -> this.then(MaxIntrinsicHeightModifier)
}

fun Modifier.requiredWidth(intrinsicSize: IntrinsicSize) = when (intrinsicSize) {
    IntrinsicSize.Min -> this.then(RequiredMinIntrinsicWidthModifier)
    IntrinsicSize.Max -> this.then(RequiredMaxIntrinsicWidthModifier)
}

fun Modifier.requiredHeight(intrinsicSize: IntrinsicSize) = when (intrinsicSize) {
    IntrinsicSize.Min -> this.then(RequiredMinIntrinsicHeightModifier)
    IntrinsicSize.Max -> this.then(RequiredMaxIntrinsicHeightModifier)
}

enum class IntrinsicSize { Min, Max }

private object MinIntrinsicWidthModifier : IntrinsicSizeModifier {
    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val width = measurable.minIntrinsicWidth(constraints.maxHeight)
        return constraints.fixedWidth(width)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height)
}

private object MinIntrinsicHeightModifier : IntrinsicSizeModifier {
    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val height = measurable.minIntrinsicHeight(constraints.maxWidth)
        println("constraints: $constraints")
        return constraints.fixedHeight(height)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width)
}

private object MaxIntrinsicWidthModifier : IntrinsicSizeModifier {
    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val width = measurable.maxIntrinsicWidth(constraints.maxHeight)
        return constraints.fixedWidth(width)
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height)
}

private object MaxIntrinsicHeightModifier : IntrinsicSizeModifier {
    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val height = measurable.maxIntrinsicHeight(constraints.maxWidth)
        return constraints.fixedHeight(height)
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width)
}

private object RequiredMinIntrinsicWidthModifier : IntrinsicSizeModifier {
    override val enforceIncoming: Boolean = false

    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val width = measurable.minIntrinsicWidth(constraints.maxHeight)
        return constraints.fixedWidth(width)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height)
}

private object RequiredMinIntrinsicHeightModifier : IntrinsicSizeModifier {
    override val enforceIncoming: Boolean = false

    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val height = measurable.minIntrinsicHeight(constraints.maxWidth)
        return constraints.fixedHeight(height)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width)
}

private object RequiredMaxIntrinsicWidthModifier : IntrinsicSizeModifier {
    override val enforceIncoming: Boolean = false

    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val width = measurable.maxIntrinsicWidth(constraints.maxHeight)
        return constraints.fixedWidth(width)
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height)
}

private object RequiredMaxIntrinsicHeightModifier : IntrinsicSizeModifier {
    override val enforceIncoming: Boolean = false

    override fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints {
        val height = measurable.maxIntrinsicHeight(constraints.maxWidth)
        return constraints.fixedHeight(height)
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width)
}

private interface IntrinsicSizeModifier : LayoutModifier {
    val enforceIncoming: Boolean get() = true

    fun MeasureScope.calculateContentConstraints(
        measurable: Measurable,
        constraints: Constraints
    ): Constraints

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val contentConstraints = calculateContentConstraints(measurable, constraints)
        val placeable = measurable.measure(
            if (enforceIncoming) constraints.constrain(contentConstraints) else contentConstraints
        )
        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width)
}

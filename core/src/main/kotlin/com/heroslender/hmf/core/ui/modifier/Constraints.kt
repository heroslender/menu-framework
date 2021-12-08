package com.heroslender.hmf.core.ui.modifier

data class Constraints(
    val minWidth: Int = 0,
    val maxWidth: Int = Infinity,
    val minHeight: Int = 0,
    val maxHeight: Int = Infinity,
) {
    fun constrainWidth(width: Int) = width.coerceIn(minWidth, maxWidth)

    fun constrainHeight(height: Int) = height.coerceIn(minHeight, maxHeight)

    fun constrain(other: Constraints): Constraints = Constraints(
        minWidth = other.minWidth.coerceIn(minWidth, maxWidth),
        maxWidth = other.maxWidth.coerceIn(minWidth, maxWidth),
        minHeight = other.minHeight.coerceIn(minHeight, maxHeight),
        maxHeight = other.maxHeight.coerceIn(minHeight, maxHeight),
    )

    fun fixedHeight(height: Int): Constraints = copy(minHeight = height, maxHeight = height)

    fun fixedWidth(width: Int): Constraints = copy(minWidth = width, maxWidth = width)

    companion object {
        val Default: Constraints = Constraints()

        /**
         * Unlimited size constraint.
         */
        const val Infinity: Int = Int.MAX_VALUE
    }
}

fun Constraints.offset(horizontal: Int = 0, vertical: Int = 0) = Constraints(
    (minWidth + horizontal).coerceAtLeast(0),
    addMaxWithMinimum(maxWidth, horizontal),
    (minHeight + vertical).coerceAtLeast(0),
    addMaxWithMinimum(maxHeight, vertical)
)

private fun addMaxWithMinimum(max: Int, value: Int): Int {
    return if (max == Constraints.Infinity) {
        max
    } else {
        (max + value).coerceAtLeast(0)
    }
}
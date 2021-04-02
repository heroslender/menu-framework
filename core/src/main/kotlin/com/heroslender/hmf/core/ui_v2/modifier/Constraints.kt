package com.heroslender.hmf.core.ui_v2.modifier

data class Constraints(
    val minWidth: Int = 0,
    val maxWidth: Int = Infinity,
    val minHeight: Int = 0,
    val maxHeight: Int = Infinity,
) {
    fun constrainWidth(width: Int) = width.coerceIn(minWidth, maxWidth)

    fun constrainHeight(height: Int) = height.coerceIn(minHeight, maxHeight)

    companion object {
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
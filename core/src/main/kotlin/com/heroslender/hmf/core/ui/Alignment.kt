package com.heroslender.hmf.core.ui

interface Alignment {
    fun align(width: Int, height: Int): PositionOffset

    interface Directional {
        fun align(size: Int): Int
    }

    interface Vertical : Directional {
        companion object {
            val Top: Vertical = AlignmentImpl.Vertical(0)
            val Center: Vertical = AlignmentImpl.Vertical(1)
            val Bottom: Vertical = AlignmentImpl.Vertical(2)
        }
    }

    interface Horizontal : Directional {
        companion object {
            val Start: Horizontal = AlignmentImpl.Horizontal(0)
            val Center: Horizontal = AlignmentImpl.Horizontal(1)
            val End: Horizontal = AlignmentImpl.Horizontal(2)
        }
    }

    companion object {
        val TopStart: Alignment = AlignmentImpl(0, 0)
        val TopCenter: Alignment = AlignmentImpl(0, 1)
        val TopEnd: Alignment = AlignmentImpl(0, 2)
        val CenterStart: Alignment = AlignmentImpl(1, 0)
        val Center: Alignment = AlignmentImpl(1, 1)
        val CenterEnd: Alignment = AlignmentImpl(1, 2)
        val BottomStart: Alignment = AlignmentImpl(2, 0)
        val BottomCenter: Alignment = AlignmentImpl(2, 1)
        val BottomEnd: Alignment = AlignmentImpl(2, 2)
    }
}

data class PositionOffset(val x: Int, val y: Int)

/**
 * 0 - Aligns at the start
 * 1 - Aligns centered
 * 2 - Aligns at the end
 */
private data class AlignmentImpl(
    val verticalMod: Int,
    val horizontalMod: Int,
) : Alignment {
    override fun align(width: Int, height: Int): PositionOffset {
        val centerX = width / 2
        val centerY = height / 2

        return PositionOffset(centerX * horizontalMod, centerY * verticalMod)
    }

    data class Vertical(
        val mod: Int,
    ) : Alignment.Vertical {
        override fun align(size: Int): Int {
            val center = size / 2

            return center * mod
        }
    }

    data class Horizontal(
        val mod: Int,
    ) : Alignment.Horizontal {
        override fun align(size: Int): Int {
            val center = size / 2

            return center * mod
        }
    }
}

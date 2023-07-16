package com.heroslender.hmf.core.ui

import kotlin.math.min

interface Arrangement {
    val spacing: Int get() = 0

    fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray)

    interface Horizontal : Arrangement

    interface Vertical : Arrangement

    interface HorizontalOrVertical : Horizontal, Vertical

    companion object {
        val Start: Horizontal = object : Horizontal {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeStart(sizes, outPositions)
            }
        }

        val End: Horizontal = object : Horizontal {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeEnd(totalSize, sizes, outPositions)
            }
        }

        val Top: Vertical = object : Vertical {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeStart(sizes, outPositions)
            }
        }

        val Bottom: Vertical = object : Vertical {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeEnd(totalSize, sizes, outPositions)
            }
        }

        val Center: HorizontalOrVertical = object : HorizontalOrVertical {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeCenter(totalSize, sizes, outPositions)
            }
        }

        val SpaceEvenly: HorizontalOrVertical = object : HorizontalOrVertical {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeSpaceEvenly(totalSize, sizes, outPositions)
            }
        }

        val SpaceBetween: HorizontalOrVertical = object : HorizontalOrVertical {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeSpaceBetween(totalSize, sizes, outPositions)
            }
        }

        val SpaceAround: HorizontalOrVertical = object : HorizontalOrVertical {
            override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                arrangeSpaceAround(totalSize, sizes, outPositions)
            }
        }

        fun spacedBy(space: Int): HorizontalOrVertical =
            SpacedAligned(space) { size ->
                Alignment.Horizontal.Start.align(size)
            }


        fun spacedBy(space: Int, alignment: Alignment.Horizontal): Horizontal =
            SpacedAligned(space) { size ->
                alignment.align(size)
            }

        fun spacedBy(space: Int, alignment: Alignment.Vertical): Vertical =
            SpacedAligned(space) { size ->
                alignment.align(size)
            }

        internal data class SpacedAligned(
            val space: Int,
            val alignment: ((Int) -> Int)?
        ) : HorizontalOrVertical {
            override val spacing = space
            override fun arrange(
                totalSize: Int,
                sizes: IntArray,
                outPositions: IntArray
            ) {
                if (sizes.isEmpty()) return
                var occupied = 0
                var lastSpace = 0
                sizes.forEachIndexed { index, it ->
                    outPositions[index] = min(occupied, totalSize - it)
                    lastSpace = min(space, totalSize - outPositions[index] - it)
                    occupied = outPositions[index] + it + lastSpace
                }
                occupied -= lastSpace
                if (alignment != null && occupied < totalSize) {
                    val groupPosition = alignment.invoke(totalSize - occupied)
                    for (index in outPositions.indices) {
                        outPositions[index] += groupPosition
                    }
                }
            }
            override fun toString() =
                "Arrangement#spacedAligned($space, $alignment)"
        }

        internal fun arrangeStart(sizes: IntArray, outSizes: IntArray) {
            var start = 0
            sizes.forEachIndexed { index, size ->
                outSizes[index] = start
                start += size
            }
        }

        internal fun arrangeCenter(totalSize: Int, sizes: IntArray, outSizes: IntArray) {
            val usedSize = sizes.fold(0) { acc, next -> acc + next }
            var start = (totalSize - usedSize) / 2
            sizes.forEachIndexed { index, size ->
                outSizes[index] = start
                start += size
            }
        }

        internal fun arrangeEnd(totalSize: Int, sizes: IntArray, outSizes: IntArray) {
            val usedSize = sizes.fold(0) { acc, next -> acc + next }
            var start = totalSize - usedSize
            sizes.forEachIndexed { index, size ->
                outSizes[index] = start
                start += size
            }
        }

        internal fun arrangeSpaceEvenly(totalSize: Int, sizes: IntArray, outSizes: IntArray) {
            val usedSize = sizes.fold(0) { acc, next -> acc + next }
            val gapSize = (totalSize - usedSize) / (sizes.size + 1)
            var start = gapSize
            sizes.forEachIndexed { index, size ->
                outSizes[index] = start
                start += size + gapSize
            }
        }

        internal fun arrangeSpaceBetween(
            totalSize: Int,
            size: IntArray,
            outPosition: IntArray
        ) {
            if (size.isEmpty()) return
            val consumedSize = size.fold(0) { a, b -> a + b }
            val noOfGaps = maxOf(size.lastIndex, 1)
            val gapSize = (totalSize - consumedSize) / noOfGaps
            var current = 0
            size.forEachIndexed { index, it ->
                outPosition[index] = current
                current += it + gapSize
            }
        }
        internal fun arrangeSpaceAround(
            totalSize: Int,
            size: IntArray,
            outPosition: IntArray
        ) {
            val consumedSize = size.fold(0) { a, b -> a + b }
            val gapSize = if (size.isNotEmpty()) {
                (totalSize - consumedSize) / size.size
            } else {
                0
            }
            var current = gapSize / 2
            size.forEachIndexed { index, it ->
                outPosition[index] = current
                current += it + gapSize
            }
        }
    }
}

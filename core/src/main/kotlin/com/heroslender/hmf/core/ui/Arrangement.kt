package com.heroslender.hmf.core.ui

interface Arrangement {
    fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray)

    interface Horizontal : Arrangement {
        companion object {
            val Start: Horizontal = object : Horizontal {
                override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                    arrangeStart(sizes, outPositions)
                }
            }

            val Center: Horizontal = object : Horizontal {
                override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                    arrangeCenter(totalSize, sizes, outPositions)
                }
            }

            val End: Horizontal = object : Horizontal {
                override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                    arrangeEnd(totalSize, sizes, outPositions)
                }
            }
        }
    }

    interface Vertical : Arrangement {

        companion object {
            val Top: Vertical = object : Vertical {
                override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                    arrangeStart(sizes, outPositions)
                }
            }

            val Center: Vertical = object : Vertical {
                override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                    arrangeCenter(totalSize, sizes, outPositions)
                }
            }

            val Bottom: Vertical = object : Vertical {
                override fun arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
                    arrangeEnd(totalSize, sizes, outPositions)
                }
            }
        }
    }


    companion object {

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
    }
}

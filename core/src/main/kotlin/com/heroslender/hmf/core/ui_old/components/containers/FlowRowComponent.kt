@file:Suppress("FunctionName", "unused")

package com.heroslender.hmf.core.ui_old.components.containers

import com.heroslender.hmf.core.ui_old.Component
import com.heroslender.hmf.core.ui_old.Composable
import com.heroslender.hmf.core.ui_old.modifier.Modifier
import com.heroslender.hmf.core.ui_old.modifier.horizontal
import com.heroslender.hmf.core.ui_old.modifier.vertical

/**
 * Instantiates a new [FlowRowComponent] and adds it
 * to the component tree.
 */
fun Composable.FlowRow(
    modifier: Modifier = Modifier,
    content: Composable.() -> Unit,
) {
    val row = FlowRowComponent(
        parent = this,
        modifier = modifier,
        builder = content,
    )
    addChild(row)
}

/**
 * A [RowComponent] that will wrap its children into a
 * new row if they overflow the available space.
 */
class FlowRowComponent(
    parent: Composable?,
    modifier: Modifier = Modifier,
    builder: Composable.() -> Unit,
) : RowComponent(parent, modifier, builder) {
    override val contentWidth: Int
        get() {
            val available = availableWidth
            var max = 0
            var width = 0
            children.forEach {
                val childWidth = it.contentWidth + it.modifier.margin.horizontal + it.modifier.padding.horizontal
                if (width + childWidth > available) {
                    if (width > max) {
                        max = width
                    }

                    width = 0
                }

                width += childWidth
            }

            return max
        }
    override val contentHeight: Int
        get() {
            val available = availableWidth
            val availableHeight = availableHeight
            var height = 0
            var maxHeight = 0
            var width = 0
            for (it in children) {
                val childWidth = it.contentWidth + it.modifier.margin.horizontal + it.modifier.padding.horizontal
                val childHeight = it.contentHeight + it.modifier.margin.vertical + it.modifier.padding.vertical
                if (width + childWidth > available) {
                    if (height + maxHeight > availableHeight) {
                        break
                    }

                    height += maxHeight
                    width = 0
                    maxHeight = 0
                }

                if (childHeight > maxHeight) {
                    maxHeight = childHeight
                }
                width += childWidth
            }

            return if (height + maxHeight > availableHeight) {
                height
            } else {
                height + maxHeight
            }
        }

    override fun computeChildrenSizes(children: List<Component>, availableWidth: Int, availableHeight: Int) {
        var height = 0
        var maxHeight = 0
        var width = 0
        val row = mutableListOf<Component>()
        for (it in children) {
            val childWidth = it.contentWidth + it.modifier.padding.horizontal + it.modifier.margin.horizontal
            val childHeight = it.contentHeight + it.modifier.padding.vertical + it.modifier.margin.vertical
            if (width + childWidth > availableWidth) {
                if (height + maxHeight > availableHeight) {
                    break
                }

                super.computeChildrenSizes(row, availableWidth, maxHeight)
                height += maxHeight
                width = 0
                maxHeight = 0
                row.clear()
            }

            row.add(it)

            if (childHeight > maxHeight) {
                maxHeight = childHeight
            }
            width += childWidth
        }

        if (row.isNotEmpty() && height + maxHeight <= availableHeight) {
            super.computeChildrenSizes(row, availableWidth, maxHeight)
        }
    }

    override fun computeHorizontalPositions(
        componentOffX: Int,
        componentOffY: Int,
        children: List<Component>,
    ) {
        val availableWidth = availableWidth
        val availableHeight = availableHeight

        var height = 0
        var maxHeight = 0
        var width = 0
        val row = mutableListOf<Component>()
        for (it in children) {
            val childWidth = it.width + it.modifier.margin.horizontal
            val childHeight = it.height + it.modifier.margin.vertical
            if (width + childWidth > availableWidth) {
                if (height + maxHeight > availableHeight) {
                    break
                }

                super.computeHorizontalPositions(componentOffX, componentOffY + height, row)
                height += maxHeight
                width = 0
                maxHeight = 0
                row.clear()
            }

            row.add(it)

            if (childHeight > maxHeight) {
                maxHeight = childHeight
            }
            width += childWidth
        }

        if (row.isNotEmpty()) {
            super.computeHorizontalPositions(componentOffX, componentOffY + height, row)
        }
    }
}
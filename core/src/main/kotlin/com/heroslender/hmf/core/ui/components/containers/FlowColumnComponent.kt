@file:Suppress("FunctionName", "unused")

package com.heroslender.hmf.core.ui.components.containers

import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.horizontal
import com.heroslender.hmf.core.ui.modifier.vertical

/**
 * Instantiates a new [FlowColumnComponent] and adds it
 * to the component tree.
 */
fun Composable.FlowColumn(
    modifier: Modifier = Modifier,
    content: Composable.() -> Unit,
) {
    val row = FlowColumnComponent(
        parent = this,
        modifier = modifier,
        builder = content,
    )
    addChild(row)
}

/**
 * A [ColumnComponent] that will wrap its children into a
 * new column if they overflow the available space.
 */
class FlowColumnComponent(
    parent: Composable?,
    modifier: Modifier = Modifier,
    builder: Composable.() -> Unit,
) : ColumnComponent(parent, modifier, builder) {

    override val contentWidth: Int
        get() {
            val availableWidth = availableWidth
            val availableHeight = availableHeight
            var height = 0
            var width = 0
            var maxWidth = 0
            for (it in children) {
                val childWidth = it.contentWidth + it.modifier.margin.horizontal + it.modifier.padding.horizontal
                val childHeight = it.contentHeight + it.modifier.margin.vertical + it.modifier.padding.vertical
                if (height + childHeight > availableHeight) {
                    if (width + maxWidth > availableWidth) {
                        break
                    }

                    width += maxWidth
                    height = 0
                    maxWidth = 0
                }

                if (childWidth > maxWidth) {
                    maxWidth = childWidth
                }
                height += childHeight
            }

            return if (width + maxWidth > availableWidth) {
                width
            } else {
                width + maxWidth
            }
        }

    override val contentHeight: Int
        get() {
            val availableHeight = availableHeight
            var maxHeight = 0
            var height = 0
            children.forEach {
                val childHeight = it.contentHeight + it.modifier.margin.vertical + it.modifier.padding.vertical
                if (height + childHeight > availableHeight) {
                    if (height > maxHeight) {
                        maxHeight = height
                    }

                    height = 0
                }

                height += childHeight
            }

            return maxHeight
        }

    override fun computeChildrenSizes(children: List<Component>, availableWidth: Int, availableHeight: Int) {
        var height = 0
        var width = 0
        var maxWidth = 0
        val row = mutableListOf<Component>()
        for (it in children) {
            val childWidth = it.contentWidth + it.modifier.padding.horizontal + it.modifier.margin.horizontal
            val childHeight = it.contentHeight + it.modifier.padding.vertical + it.modifier.margin.vertical
            if (height + childHeight > availableHeight) {
                if (width + maxWidth > availableWidth) {
                    break
                }
                super.computeChildrenSizes(row, maxWidth, availableHeight)
                width += maxWidth
                height = 0
                maxWidth = 0
                row.clear()
            }

            row.add(it)

            if (childWidth > maxWidth) {
                maxWidth = childWidth
            }
            height += childHeight
        }

        if (row.isNotEmpty() && width + maxWidth <= availableWidth) {
            super.computeChildrenSizes(row, maxWidth, availableHeight)
        }
    }

    override fun computeVerticalPositions(
        componentOffX: Int,
        componentOffY: Int,
        children: List<Component>,
    ) {
        val availableWidth = availableWidth
        val availableHeight = availableHeight
        var height = 0
        var width = 0
        var maxWidth = 0
        val row = mutableListOf<Component>()
        for (it in children) {
            val childWidth = it.width + it.modifier.margin.horizontal
            val childHeight = it.height + it.modifier.margin.vertical
            if (height + childHeight > availableHeight) {
                if (width + maxWidth > availableWidth) {
                    break
                }
                super.computeVerticalPositions(componentOffX + width, componentOffY, row)
                width += maxWidth
                height = 0
                maxWidth = 0
                row.clear()
            }

            row.add(it)

            if (childWidth > maxWidth) {
                maxWidth = childWidth
            }
            height += childHeight
        }

        if (row.isNotEmpty()) {
            super.computeVerticalPositions(componentOffX + width, componentOffY, row)
        }
    }
}
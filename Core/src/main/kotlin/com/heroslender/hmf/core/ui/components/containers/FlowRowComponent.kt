@file:Suppress("FunctionName")

package com.heroslender.hmf.core.ui.components.containers

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.marginHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginVertical
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingVertical

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
                val childWidth = it.contentWidth + it.modifier.marginHorizontal + it.modifier.paddingHorizontal
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
                val childWidth = it.contentWidth + it.modifier.marginHorizontal + it.modifier.paddingHorizontal
                val childHeight = it.contentHeight + it.modifier.marginVertical + it.modifier.paddingVertical
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
            val childWidth = it.contentWidth + it.modifier.paddingHorizontal + it.modifier.marginHorizontal
            val childHeight = it.contentHeight + it.modifier.paddingVertical + it.modifier.marginVertical
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
        context: RenderContext,
    ) {
        val availableWidth = availableWidth
        val availableHeight = availableHeight

        var height = 0
        var maxHeight = 0
        var width = 0
        val row = mutableListOf<Component>()
        for (it in children) {
            val childWidth = it.width + it.modifier.marginHorizontal
            val childHeight = it.height + it.modifier.marginVertical
            if (width + childWidth > availableWidth) {
                if (height + maxHeight > availableHeight) {
                    break
                }

                super.computeHorizontalPositions(componentOffX, componentOffY + height, row, context)
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
            super.computeHorizontalPositions(componentOffX, componentOffY + height, row, context)
        }
    }
}
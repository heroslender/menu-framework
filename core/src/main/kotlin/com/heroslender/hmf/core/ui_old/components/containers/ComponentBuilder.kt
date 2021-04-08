package com.heroslender.hmf.core.ui_old.components.containers

import com.heroslender.hmf.core.ui_old.Component
import com.heroslender.hmf.core.ui_old.Composable
import com.heroslender.hmf.core.ui_old.DrawableComponent
import com.heroslender.hmf.core.ui_old.Orientation
import com.heroslender.hmf.core.ui_old.modifier.*
import kotlin.math.max

abstract class ComponentBuilder(
    parent: Composable?,
    override val modifier: Modifier = Modifier,
    private val orientation: Orientation = Orientation.HORIZONTAL,
    val builder: Composable.() -> Unit,
) : DrawableComponent(parent), Composable {
    private val _children: MutableList<Component> = mutableListOf()
    override val children: List<Component>
        get() = _children

    override val contentWidth: Int
        get() = if (orientation == Orientation.HORIZONTAL) {
            _children.map { it.contentWidth + it.modifier.margin.horizontal + it.modifier.padding.horizontal }.sum()
        } else {
            _children.map { it.contentWidth + it.modifier.margin.horizontal + it.modifier.padding.horizontal }
                .maxOrNull()
                ?: 0
        }

    override val contentHeight: Int
        get() = if (orientation == Orientation.HORIZONTAL) {
            _children.map { it.contentHeight + it.modifier.margin.vertical + it.modifier.padding.vertical }.maxOrNull()
                ?: 0
        } else {
            _children.map { it.contentHeight + it.modifier.margin.vertical + it.modifier.padding.vertical }.sum()
        }

    override fun render(): Boolean {
        var rendered = super.render()
        for (child in _children) {
            rendered = rendered or child.render()
        }

        return rendered
    }

    override fun compose() {
        disposeChildren()
        _children.clear()

        builder()

        children.filterIsInstance<Composable>().forEach(Composable::compose)
    }

    override fun reRender(offsetX: Int, offsetY: Int) {
        super.reRender(offsetX, offsetY)

        computeChildrenSizes(_children, width - modifier.padding.horizontal, height - modifier.padding.vertical)

        val currOffX = offsetX + modifier.padding.left
        val currOffY = offsetY + modifier.padding.top

        val childrenIterator = _children.iterator()
        for (child in childrenIterator) {
            if (child.width == 0 || child.height == 0) {
                // Overflow
                childrenIterator.remove()
            }
        }

        if (orientation == Orientation.HORIZONTAL) {
            computeHorizontalPositions(currOffX, currOffY, this._children)
        } else {
            computeVerticalPositions(currOffX, currOffY, this._children)
        }
    }

    open fun computeChildrenSizes(children: List<Component>, availableWidth: Int, availableHeight: Int) {
        var widthFillCount = children.count { it.modifier.width === Fill }
        var freeWidth = availableWidth - if (widthFillCount > 0 && orientation == Orientation.HORIZONTAL) {
            children.sumBy { child ->
                when (child.modifier.width) {
                    is FixedSize -> child.modifier.width.value + child.modifier.margin.horizontal
                    is FitContent -> child.contentWidth + child.modifier.padding.horizontal + child.modifier.margin.horizontal
                    else -> 0
                }
            }
        } else 0

        if (widthFillCount > 0 && orientation == Orientation.HORIZONTAL) {
            var fillSize = freeWidth / widthFillCount

            for (child in children) {
                if (child.modifier.width is Fill) {
                    val childFullWidth =
                        child.contentWidth + child.modifier.padding.horizontal + child.modifier.margin.horizontal
                    if (childFullWidth > fillSize) {
                        freeWidth -= childFullWidth
                        widthFillCount--
                        if (widthFillCount <= 0) {
                            break
                        }
                        fillSize = freeWidth / widthFillCount
                    }
                }
            }
        }

        var heightFillCount = children.count { it.modifier.height === Fill }
        var freeHeight = availableHeight - if (heightFillCount > 0 && orientation == Orientation.VERTICAL) {
            children.sumBy { child ->
                when (child.modifier.height) {
                    is FixedSize -> child.modifier.height.value + child.modifier.margin.vertical
                    is FitContent -> child.contentHeight + child.modifier.padding.vertical + child.modifier.margin.vertical
                    else -> 0
                }
            }
        } else 0

        if (heightFillCount > 0 && orientation == Orientation.VERTICAL) {
            var fillSize = freeHeight / heightFillCount

            for (child in children) {
                if (child.modifier.height is Fill) {
                    val childFullHeight =
                        child.contentHeight + child.modifier.padding.vertical + child.modifier.margin.vertical
                    if (childFullHeight > fillSize) {
                        freeHeight -= childFullHeight
                        heightFillCount--
                        if (heightFillCount <= 0) {
                            break
                        }
                        fillSize = freeHeight / heightFillCount
                    }
                }
            }
        }

        // Compute sizes
        for (child in children) {
            computeChildSize(child, freeWidth, widthFillCount, freeHeight, heightFillCount)
        }
    }

    private fun computeChildSize(
        child: Component,
        freeWidth: Int,
        widthFillCount: Int,
        freeHeight: Int,
        heightFillCount: Int,
    ) {
        child.width = when (child.modifier.width) {
            is FixedSize -> child.modifier.width.value
            is FitContent -> child.contentWidth + child.modifier.padding.horizontal
            is Fill -> {
                val childFullWidth = child.contentWidth + child.modifier.padding.horizontal
                val fillWidth = if (orientation == Orientation.HORIZONTAL) {
                    freeWidth / widthFillCount
                } else {
                    freeWidth
                } - child.modifier.margin.horizontal

                max(childFullWidth, fillWidth)
            }
        }

        child.height = when (child.modifier.height) {
            is FixedSize -> child.modifier.height.value
            is FitContent -> child.contentHeight + child.modifier.padding.vertical
            is Fill -> {
                val childFullHeight = child.contentHeight + child.modifier.padding.vertical
                val fillHeight = if (orientation == Orientation.VERTICAL) {
                    freeHeight / heightFillCount
                } else {
                    freeHeight
                } - child.modifier.margin.vertical

                max(childFullHeight, fillHeight)
            }
        }
    }

    open fun computeHorizontalPositions(
        componentOffX: Int,
        componentOffY: Int,
        children: List<Component>,
    ) {
        var currOffX: Int = componentOffX
        val widthFillCount = children.count { it.modifier.width === Fill }

        if (widthFillCount == 0 && children.any { it.modifier.horizontalAlignment != HorizontalAlignment.START }) {
            var startSize = 0
            val centerCount = children.count { it.modifier.horizontalAlignment == HorizontalAlignment.CENTER } + 1
            val centerSize =
                children.sumBy { if (it.modifier.horizontalAlignment == HorizontalAlignment.CENTER) it.width + it.modifier.margin.horizontal else 0 }
            val endSize =
                children.sumBy { if (it.modifier.horizontalAlignment == HorizontalAlignment.END) it.width + it.modifier.margin.horizontal else 0 }
            var isPlacingEnd = false

            for (child in children.sortedBy { it.modifier.horizontalAlignment }) {
                val mod = child.modifier
                currOffX += mod.margin.left

                when (child.modifier.horizontalAlignment) {
                    HorizontalAlignment.START -> {
                        // won't change
                        startSize = currOffX - componentOffX + child.width + mod.margin.right
                    }
                    HorizontalAlignment.CENTER -> {
                        val step =
                            (width - modifier.padding.horizontal - startSize - endSize - centerSize) / centerCount
                        currOffX += step
                    }
                    HorizontalAlignment.END -> {
                        if (!isPlacingEnd) {
                            isPlacingEnd = true

                            val step =
                                (width - modifier.padding.horizontal - startSize - endSize - centerSize) / centerCount
                            currOffX += step
                        }
                    }
                }

                val offY = when (child.modifier.verticalAlignment) {
                    VerticalAlignment.TOP -> {
                        // won't change
                        componentOffY + mod.margin.top
                    }
                    VerticalAlignment.CENTER -> {
                        componentOffY + (height - modifier.padding.vertical - child.height) / 2
                    }
                    VerticalAlignment.BOTTOM -> {
                        componentOffY + height - modifier.padding.vertical - child.height
                    }
                }
                child.reRender(currOffX, offY)
                currOffX += child.width + mod.margin.right
            }
        } else {
            for (child in children) {
                val mod = child.modifier
                currOffX += mod.margin.left
                val offY = when (child.modifier.verticalAlignment) {
                    VerticalAlignment.TOP -> {
                        // won't change
                        componentOffY + mod.margin.top
                    }
                    VerticalAlignment.CENTER -> {
                        componentOffY + (height - modifier.padding.vertical - child.height) / 2
                    }
                    VerticalAlignment.BOTTOM -> {
                        componentOffY + height - modifier.padding.vertical - child.height
                    }
                }
                child.reRender(currOffX, offY)
                currOffX += child.width + mod.margin.right
            }
        }
    }

    open fun computeVerticalPositions(
        componentOffX: Int,
        componentOffY: Int,
        children: List<Component>,
    ) {
        var currOffY: Int = componentOffY
        val heightFillCount = children.count { it.modifier.height === Fill }

        if (heightFillCount == 0 && children.any { it.modifier.verticalAlignment != VerticalAlignment.TOP }) {
            var topSize = 0
            val centerCount = children.count { it.modifier.verticalAlignment == VerticalAlignment.CENTER } + 1
            val centerSize =
                children.sumBy { if (it.modifier.verticalAlignment == VerticalAlignment.CENTER) it.height + it.modifier.margin.vertical else 0 }
            val bottomSize =
                children.sumBy { if (it.modifier.verticalAlignment == VerticalAlignment.BOTTOM) it.height + it.modifier.margin.vertical else 0 }
            var isPlacingEnd = false

            for (child in children.sortedBy { it.modifier.verticalAlignment }) {
                val mod = child.modifier
                currOffY += mod.margin.top

                when (child.modifier.verticalAlignment) {
                    VerticalAlignment.TOP -> {
                        // won't change
                        topSize = currOffY - componentOffY + child.height + mod.margin.bottom
                    }
                    VerticalAlignment.CENTER -> {
                        val step =
                            (height - modifier.padding.vertical - topSize - bottomSize - centerSize) / centerCount
                        currOffY += step
                    }
                    VerticalAlignment.BOTTOM -> {
                        if (!isPlacingEnd) {
                            isPlacingEnd = true

                            val step =
                                (height - modifier.padding.vertical - topSize - bottomSize - centerSize) / centerCount
                            currOffY += step
                        }
                    }
                }

                val offX = when (child.modifier.horizontalAlignment) {
                    HorizontalAlignment.START -> {
                        // won't change
                        componentOffX + mod.margin.left
                    }
                    HorizontalAlignment.CENTER -> {
                        componentOffX + (width - modifier.margin.horizontal - child.width) / 2
                    }
                    HorizontalAlignment.END -> {
                        componentOffX + width - modifier.margin.horizontal - child.width
                    }
                }
                child.reRender(offX, currOffY)
                currOffY += child.height + mod.margin.bottom
            }
        } else {
            for (child in children) {
                val mod = child.modifier
                currOffY += mod.margin.top
                val offX = when (child.modifier.horizontalAlignment) {
                    HorizontalAlignment.START -> {
                        // won't change
                        componentOffX + mod.margin.left
                    }
                    HorizontalAlignment.CENTER -> {
                        componentOffX + (width - modifier.margin.horizontal - child.width) / 2
                    }
                    HorizontalAlignment.END -> {
                        componentOffX + width - modifier.margin.horizontal - child.width
                    }
                }

                child.reRender(offX, currOffY)
                currOffY += child.height + mod.margin.bottom
            }
        }
    }

    override fun dispose() {
        super<DrawableComponent>.dispose()

        disposeChildren()
    }

    override fun addChild(child: Component) {
        _children.add(child)
    }

    private fun disposeChildren() {
        for (child in children) {
            child.dispose()
        }
    }
}
package com.heroslender.hmf.core.ui.components.containers

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.DrawableComponent
import com.heroslender.hmf.core.ui.Orientation
import com.heroslender.hmf.core.ui.modifier.*
import com.heroslender.hmf.core.ui.modifier.modifiers.marginHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginVertical
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingVertical
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
            _children.map { it.contentWidth + it.modifier.marginHorizontal + it.modifier.paddingHorizontal }.sum()
        } else {
            _children.map { it.contentWidth + it.modifier.marginHorizontal + it.modifier.paddingHorizontal }.maxOrNull()
                ?: 0
        }

    override val contentHeight: Int
        get() = if (orientation == Orientation.HORIZONTAL) {
            _children.map { it.contentHeight + it.modifier.marginVertical + it.modifier.paddingVertical }.maxOrNull()
                ?: 0
        } else {
            _children.map { it.contentHeight + it.modifier.marginVertical + it.modifier.paddingVertical }.sum()
        }

    override fun render(): Boolean {
        super.render()

        var rendered = false
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

        computeChildrenSizes(_children, width - modifier.paddingHorizontal, height - modifier.paddingVertical)

        val currOffX = offsetX + modifier.paddingLeft
        val currOffY = offsetY + modifier.paddingTop

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
                    is FixedSize -> child.modifier.width.value + child.modifier.marginHorizontal
                    is FitContent -> child.contentWidth + child.modifier.paddingHorizontal + child.modifier.marginHorizontal
                    else -> 0
                }
            }
        } else 0

        if (widthFillCount > 0 && orientation == Orientation.HORIZONTAL) {
            var fillSize = freeWidth / widthFillCount

            for (child in children) {
                if (child.modifier.width is Fill) {
                    val childFullWidth =
                        child.contentWidth + child.modifier.paddingHorizontal + child.modifier.marginHorizontal
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
                    is FixedSize -> child.modifier.height.value + child.modifier.marginVertical
                    is FitContent -> child.contentHeight + child.modifier.paddingVertical + child.modifier.marginVertical
                    else -> 0
                }
            }
        } else 0

        if (heightFillCount > 0 && orientation == Orientation.VERTICAL) {
            var fillSize = freeHeight / heightFillCount

            for (child in children) {
                if (child.modifier.height is Fill) {
                    val childFullHeight =
                        child.contentHeight + child.modifier.paddingVertical + child.modifier.marginVertical
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
            is FitContent -> child.contentWidth + child.modifier.paddingHorizontal
            is Fill -> {
                val childFullWidth = child.contentWidth + child.modifier.paddingHorizontal
                val fillWidth = if (orientation == Orientation.HORIZONTAL) {
                    freeWidth / widthFillCount
                } else {
                    freeWidth
                } - child.modifier.marginHorizontal

                max(childFullWidth, fillWidth)
            }
        }

        child.height = when (child.modifier.height) {
            is FixedSize -> child.modifier.height.value
            is FitContent -> child.contentHeight + child.modifier.paddingVertical
            is Fill -> {
                val childFullHeight = child.contentHeight + child.modifier.paddingVertical
                val fillHeight = if (orientation == Orientation.VERTICAL) {
                    freeHeight / heightFillCount
                } else {
                    freeHeight
                } - child.modifier.marginVertical

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
                children.sumBy { if (it.modifier.horizontalAlignment == HorizontalAlignment.CENTER) it.width + it.modifier.marginHorizontal else 0 }
            val endSize =
                children.sumBy { if (it.modifier.horizontalAlignment == HorizontalAlignment.END) it.width + it.modifier.marginHorizontal else 0 }
            var isPlacingEnd = false

            for (child in children.sortedBy { it.modifier.horizontalAlignment }) {
                val mod = child.modifier
                currOffX += mod.marginLeft

                when (child.modifier.horizontalAlignment) {
                    HorizontalAlignment.START -> {
                        // won't change
                        startSize = currOffX - componentOffX + child.width + mod.marginRight
                    }
                    HorizontalAlignment.CENTER -> {
                        val step =
                            (width - modifier.paddingHorizontal - startSize - endSize - centerSize) / centerCount
                        currOffX += step
                    }
                    HorizontalAlignment.END -> {
                        if (!isPlacingEnd) {
                            isPlacingEnd = true

                            val step =
                                (width - modifier.paddingHorizontal - startSize - endSize - centerSize) / centerCount
                            currOffX += step
                        }
                    }
                }

                val offY = when (child.modifier.verticalAlignment) {
                    VerticalAlignment.TOP -> {
                        // won't change
                        componentOffY + mod.marginTop
                    }
                    VerticalAlignment.CENTER -> {
                        componentOffY + (height - modifier.paddingVertical - child.height) / 2
                    }
                    VerticalAlignment.BOTTOM -> {
                        componentOffY + height - modifier.paddingVertical - child.height
                    }
                }
                child.reRender(currOffX, offY)
                currOffX += child.width + mod.marginRight
            }
        } else {
            for (child in children) {
                val mod = child.modifier
                currOffX += mod.marginLeft
                val offY = when (child.modifier.verticalAlignment) {
                    VerticalAlignment.TOP -> {
                        // won't change
                        componentOffY + mod.marginTop
                    }
                    VerticalAlignment.CENTER -> {
                        componentOffY + (height - modifier.paddingVertical - child.height) / 2
                    }
                    VerticalAlignment.BOTTOM -> {
                        componentOffY + height - modifier.paddingVertical - child.height
                    }
                }
                child.reRender(currOffX, offY)
                currOffX += child.width + mod.marginRight
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
                children.sumBy { if (it.modifier.verticalAlignment == VerticalAlignment.CENTER) it.height + it.modifier.marginVertical else 0 }
            val bottomSize =
                children.sumBy { if (it.modifier.verticalAlignment == VerticalAlignment.BOTTOM) it.height + it.modifier.marginVertical else 0 }
            var isPlacingEnd = false

            for (child in children.sortedBy { it.modifier.verticalAlignment }) {
                val mod = child.modifier
                currOffY += mod.marginTop

                when (child.modifier.verticalAlignment) {
                    VerticalAlignment.TOP -> {
                        // won't change
                        topSize = currOffY - componentOffY + child.height + mod.marginBottom
                    }
                    VerticalAlignment.CENTER -> {
                        val step =
                            (height - modifier.paddingVertical - topSize - bottomSize - centerSize) / centerCount
                        currOffY += step
                    }
                    VerticalAlignment.BOTTOM -> {
                        if (!isPlacingEnd) {
                            isPlacingEnd = true

                            val step =
                                (height - modifier.paddingVertical - topSize - bottomSize - centerSize) / centerCount
                            currOffY += step
                        }
                    }
                }

                val offX = when (child.modifier.horizontalAlignment) {
                    HorizontalAlignment.START -> {
                        // won't change
                        componentOffX + mod.marginLeft
                    }
                    HorizontalAlignment.CENTER -> {
                        componentOffX + (width - modifier.marginHorizontal - child.width) / 2
                    }
                    HorizontalAlignment.END -> {
                        componentOffX + width - modifier.marginHorizontal - child.width
                    }
                }
                child.reRender(offX, currOffY)
                currOffY += child.height + mod.marginBottom
            }
        } else {
            for (child in children) {
                val mod = child.modifier
                currOffY += mod.marginTop
                val offX = when (child.modifier.horizontalAlignment) {
                    HorizontalAlignment.START -> {
                        // won't change
                        componentOffX + mod.marginLeft
                    }
                    HorizontalAlignment.CENTER -> {
                        componentOffX + (width - modifier.marginHorizontal - child.width) / 2
                    }
                    HorizontalAlignment.END -> {
                        componentOffX + width - modifier.marginHorizontal - child.width
                    }
                }

                child.reRender(offX, currOffY)
                currOffY += child.height + mod.marginBottom
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
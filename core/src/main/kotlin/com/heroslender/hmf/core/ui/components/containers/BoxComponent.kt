@file:Suppress("FunctionName")

package com.heroslender.hmf.core.ui.components.containers

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.DrawableComponent
import com.heroslender.hmf.core.ui.modifier.*
import com.heroslender.hmf.core.ui.modifier.modifiers.marginHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginVertical
import kotlin.math.max

/**
 * Instantiates a new [BoxComponent] and adds it
 * to the component tree.
 */
fun Composable.Box(
    modifier: Modifier = Modifier,
    content: Composable.() -> Unit,
) {
    val box = BoxComponent(
        parent = this,
        modifier = modifier,
        builder = content,
    )
    addChild(box)
}

/**
 * A composable that will stack its children one on top of another.
 */
open class BoxComponent(
    parent: Composable?,
    renderContext: RenderContext = parent!!.renderContext,
    modifier: Modifier = Modifier,
    val builder: Composable.() -> Unit,
) : DrawableComponent(parent, modifier, renderContext), Composable {

    private val _children: MutableList<Component> = mutableListOf()
    override val children: List<Component>
        get() = _children

    override val contentWidth: Int
        get() = children.map { it.contentWidth + it.modifier.marginHorizontal + it.modifier.padding.horizontal }
            .maxOrNull() ?: 0

    override val contentHeight: Int
        get() = children.map { it.contentHeight + it.modifier.marginVertical + it.modifier.padding.vertical }
            .maxOrNull() ?: 0

    override fun render(): Boolean {
        super.render()

        var rendered = false
        for (child in children) {
            rendered = rendered or child.render()
        }

        return rendered
    }

    override fun compose() {
        disposeChildren()
        _children.clear()

        builder()

        _children.filterIsInstance<Composable>().forEach(Composable::compose)
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

        computePositions(currOffX, currOffY, this._children)
    }

    open fun computeChildrenSizes(children: List<Component>, availableWidth: Int, availableHeight: Int) {
        for (child in children) {
            computeChildSize(child, availableWidth, availableHeight)
        }
    }

    private fun computeChildSize(
        child: Component,
        freeWidth: Int,
        freeHeight: Int,
    ) {
        child.width = when (child.modifier.width) {
            is FixedSize -> child.modifier.width.value
            is FitContent -> child.contentWidth + child.modifier.padding.horizontal
            is Fill -> {
                val childFullWidth = child.contentWidth + child.modifier.padding.horizontal
                val fillWidth = freeWidth - child.modifier.marginHorizontal

                max(childFullWidth, fillWidth)
            }
        }

        child.height = when (child.modifier.height) {
            is FixedSize -> child.modifier.height.value
            is FitContent -> child.contentHeight + child.modifier.padding.vertical
            is Fill -> {
                val childFullHeight = child.contentHeight + child.modifier.padding.vertical
                val fillHeight = freeHeight - child.modifier.marginVertical

                max(childFullHeight, fillHeight)
            }
        }
    }

    open fun computePositions(
        componentOffX: Int,
        componentOffY: Int,
        children: List<Component>,
    ) {
        for (child in children.sortedBy { it.modifier.horizontalAlignment }) {
            val mod = child.modifier

            val offX = when (child.modifier.horizontalAlignment) {
                HorizontalAlignment.START -> {
                    // won't change
                    componentOffX + mod.marginLeft
                }
                HorizontalAlignment.CENTER -> {
                    componentOffX + (width - modifier.padding.horizontal - child.width) / 2
                }
                HorizontalAlignment.END -> {
                    componentOffX + width - modifier.padding.horizontal - child.width
                }
            }

            val offY = when (child.modifier.verticalAlignment) {
                VerticalAlignment.TOP -> {
                    // won't change
                    componentOffY + mod.marginTop
                }
                VerticalAlignment.CENTER -> {
                    componentOffY + (height - modifier.padding.vertical - child.height) / 2
                }
                VerticalAlignment.BOTTOM -> {
                    componentOffY + height - modifier.padding.vertical - child.height
                }
            }

            child.reRender(offX, offY)
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
        for (child in _children) {
            child.dispose()
        }
    }
}
@file:Suppress("FunctionName")

package com.heroslender.hmf.core.ui.components.containers

import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.DrawableComponent
import com.heroslender.hmf.core.ui.modifier.*
import com.heroslender.hmf.core.ui.modifier.modifiers.marginHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.marginVertical
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingHorizontal
import com.heroslender.hmf.core.ui.modifier.modifiers.paddingVertical
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
class BoxComponent(
    parent: Composable?,
    override val modifier: Modifier = Modifier,
    val builder: Composable.() -> Unit,
) : DrawableComponent(parent), Composable {
    private val _children: MutableList<Component> = mutableListOf()
    override val children: List<Component>
        get() = _children

    override val contentWidth: Int
        get() = children.map { it.contentWidth + it.modifier.marginHorizontal + it.modifier.paddingHorizontal }
            .maxOrNull() ?: 0

    override val contentHeight: Int
        get() = children.map { it.contentHeight + it.modifier.marginVertical + it.modifier.paddingVertical }
            .maxOrNull() ?: 0

    override fun render(): Boolean {
        super.render()
        println("render:" + this.javaClass.simpleName + " -> " + children.size)

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

    override fun reRender(offsetX: Int, offsetY: Int, context: RenderContext) {
        super.reRender(offsetX, offsetY, context)

        computeChildrenSizes(_children, width - modifier.paddingHorizontal, height - modifier.paddingVertical)

        val currOffX = offsetX + modifier.paddingLeft
        val currOffY = offsetY + modifier.paddingTop
        println(this.javaClass.simpleName + " -> " + children.size)

        val childrenIterator = _children.iterator()
        for (child in childrenIterator) {
            if (child.width == 0 || child.height == 0) {
                // Overflow
                childrenIterator.remove()
            }
        }

        computePositions(currOffX, currOffY, this._children, context)
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
            is FitContent -> child.contentWidth + child.modifier.paddingHorizontal
            is Fill -> {
                val childFullWidth = child.contentWidth + child.modifier.paddingHorizontal
                val fillWidth = freeWidth - child.modifier.marginHorizontal

                max(childFullWidth, fillWidth)
            }
        }

        child.height = when (child.modifier.height) {
            is FixedSize -> child.modifier.height.value
            is FitContent -> child.contentHeight + child.modifier.paddingVertical
            is Fill -> {
                val childFullHeight = child.contentHeight + child.modifier.paddingVertical
                val fillHeight = freeHeight - child.modifier.marginVertical

                max(childFullHeight, fillHeight)
            }
        }
    }

    open fun computePositions(
        componentOffX: Int,
        componentOffY: Int,
        children: List<Component>,
        context: RenderContext,
    ) {
        for (child in children.sortedBy { it.modifier.horizontalAlignment }) {
            val mod = child.modifier

            val offX = when (child.modifier.horizontalAlignment) {
                HorizontalAlignment.START -> {
                    // won't change
                    componentOffX + mod.marginLeft
                }
                HorizontalAlignment.CENTER -> {
                    componentOffX + (width - modifier.paddingHorizontal - child.width) / 2
                }
                HorizontalAlignment.END -> {
                    componentOffX + width - modifier.paddingHorizontal - child.width
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

            println("$offX $offY ${child.javaClass.simpleName}")
            child.reRender(offX, offY, context)
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
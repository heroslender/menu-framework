package com.heroslender.hmf.core.ui

/**
 * A component that's composed by multiple child components.
 */
interface Composable : Component {

    val childOffsetX: Int
    val childOffsetY: Int

    /**
     * List of children this composable holds.
     */
    val children: List<Component>

    /**
     * Add a [child] to this composable component.
     */
    fun addChild(child: Component)

    /**
     * Initialize this composable composition.
     */
    fun compose()

    override fun <R> foldIn(acc: R, op: (R, Component) -> R): R {
        var a = super.foldIn(acc, op)

        for (child in children) {
            a = child.foldIn(a, op)
        }

        return a
    }

    override fun <R> foldOut(acc: R, op: (R, Component) -> R): R {
        var a = acc

        for (child in children.reversed()) {
            a = child.foldOut(a, op)
        }

        return super.foldOut(a, op)
    }
}

package com.heroslender.hmf.core.compose

import androidx.compose.runtime.AbstractApplier
import com.heroslender.hmf.core.ui.Component

internal class MenuNodeApplier(root: Component) : AbstractApplier<Component>(root) {
    override fun insertTopDown(index: Int, instance: Component) {
        // Ignored, we insert bottom-up.
    }

    override fun insertBottomUp(index: Int, instance: Component) {
        current.children.add(index, instance)
        check(instance.parent == null) {
            "$instance must not have a parent when being inserted."
        }
        instance.parent = current
    }

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun onClear() {
        current.children.clear()
    }
}
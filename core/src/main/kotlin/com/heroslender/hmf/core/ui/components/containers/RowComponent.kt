@file:Suppress("FunctionName")

package com.heroslender.hmf.core.ui.components.containers

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.Orientation
import com.heroslender.hmf.core.ui.modifier.Modifier

/**
 * Instantiates a new [RowComponent] and adds it
 * to the component tree.
 */
fun Composable.Row(
    modifier: Modifier = Modifier,
    content: Composable.() -> Unit,
) {
    val row = RowComponent(
        parent = this,
        modifier = modifier,
        builder = content,
    )
    addChild(row)
}

/**
 * A component that will dispose it's children along the `x` axis.
 *
 * If they overflow the available space, they won't be shown.
 */
open class RowComponent(
    parent: Composable?,
    modifier: Modifier = Modifier,
    builder: Composable.() -> Unit,
) : ComponentBuilder(parent, modifier, Orientation.HORIZONTAL, builder)
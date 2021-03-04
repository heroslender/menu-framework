@file:Suppress("FunctionName")

package com.heroslender.hmf.core.ui.components.containers

import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.Orientation
import com.heroslender.hmf.core.ui.modifier.Modifier

/**
 * Instantiates a new [ColumnComponent] and adds it
 * to the component tree.
 */
fun Composable.Column(
    modifier: Modifier = Modifier,
    content: Composable.() -> Unit,
) {
    val col = ColumnComponent(
        parent = this,
        modifier = modifier,
        builder = content,
    )
    addChild(col)
}


/**
 * A component that will dispose it's children along the `y` axis.
 *
 * If they overflow the available space, they won't be shown.
 */
open class ColumnComponent(
    parent: Composable?,
    modifier: Modifier = Modifier,
    builder: Composable.() -> Unit,
) : ComponentBuilder(parent, modifier, Orientation.VERTICAL, builder)

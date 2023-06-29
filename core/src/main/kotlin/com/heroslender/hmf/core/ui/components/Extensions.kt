package com.heroslender.hmf.core.ui.components

import com.heroslender.hmf.core.ui.*
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier


//inline fun Composable.appendComposable(
//    modifier: Modifier,
//    noinline content: Composable.() -> Unit,
//    transformer: Composable.() -> Unit = {},
//): Composable {
//    val node = ComposableNode(this, modifier, renderContext, content)
//    node.transformer()
//    addChild(node)
//
//    return node
//}
//
//inline fun Composable.appendComponent(
//    modifier: Modifier,
//    transformer: Component.() -> Unit = {},
//): Component {
//    val node = ComponentNode(this, modifier, renderContext)
//    node.transformer()
//    addChild(node)
//
//    return node
//}

inline fun newMeasurableGroup(
    crossinline op: MeasureScope.(measurables: List<Measurable>, constraints: Constraints) -> MeasureScope.MeasureResult,
): MeasurableGroup = object : MeasurableGroup {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureScope.MeasureResult = op(measurables, constraints)
}

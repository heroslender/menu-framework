package com.heroslender.hmf.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import com.heroslender.hmf.core.ui.Component
import com.heroslender.hmf.core.ui.LayoutNode
import com.heroslender.hmf.core.ui.MeasurableGroup
import com.heroslender.hmf.core.ui.modifier.Modifier

@Composable
inline fun Layout(
    measurableGroup: MeasurableGroup = MeasurableGroup,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
    name: String = "Unnamed",
) {
    val canvas = LocalCanvas.current
    val menu = LocalMenu.current
    ComposeNode<Component, MenuNodeApplier>(
        factory = {
            LayoutNode().apply {
                this.name = name
                this.menu = menu
            }
        },
        update = {
            set(measurableGroup) { this.measurableGroup = it }
            set(canvas) { this.canvas = it }
            set(modifier) { this.modifier = it }

            reconcile {
                flagDirty()
            }
        },
        content = content,
    )
}
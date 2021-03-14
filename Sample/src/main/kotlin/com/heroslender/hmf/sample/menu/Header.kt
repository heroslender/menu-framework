package com.heroslender.hmf.sample.menu

import com.heroslender.hmf.bukkit.map.Color
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.Image
import com.heroslender.hmf.core.ui.components.containers.Row
import com.heroslender.hmf.core.ui.modifier.HorizontalAlignment
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.*

fun Composable.NavBar(content: Composable.() -> Unit) {
    Row(Modifier.fillWidth(), content)
}

fun Composable.NavIcon(asset: String, onClick: ClickListener) {
    Image(
        asset,
        modifier = Modifier
            .backgroundColor(Color.CYAN_7)
            .border(Color.CYAN_8)
            .padding(3)
            .clickable(onClick)
    )
}

fun Composable.NavIconEnd(asset: String, onClick: ClickListener) {
    Image(
        asset,
        modifier = Modifier
            .backgroundColor(Color.CYAN_7)
            .border(Color.CYAN_8)
            .align(horizontal = HorizontalAlignment.END)
            .padding(3)
            .clickable(onClick)
    )
}
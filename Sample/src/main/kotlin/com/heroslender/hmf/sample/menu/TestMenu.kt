package com.heroslender.hmf.sample.menu

import com.heroslender.hmf.bukkit.BaseMenu
import com.heroslender.hmf.bukkit.MenuOptions
import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
import com.heroslender.hmf.bukkit.map.Color
import com.heroslender.hmf.bukkit.modifiers.clickable
import com.heroslender.hmf.bukkit.privateFor
import com.heroslender.hmf.core.Preview
import com.heroslender.hmf.core.mutableStateOf
import com.heroslender.hmf.core.ui.Alignment
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.*
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.*
import com.heroslender.hmf.core.ui.withState
import com.heroslender.hmf.sample.menu.components.ButtonBackgroundColors
import com.heroslender.hmf.sample.menu.components.Text
import com.heroslender.hmf.sample.menu.components.TextButton
import org.bukkit.entity.Player

@Preview(
    name = "Preview 1",
    width = 512,
    height = 512,
)
fun Composable.menuPreview() {
    val counter = mutableStateOf(0)

    Column(
        modifier = Modifier.fillSize().background(Color.BLUE_3)
    ) {
        Header()

        Row(
            modifier = Modifier
                .padding(5, 3)
        ) {
            TextButton("Increment") {
                counter.value++
            }

            TextButton("Decrement", modifier = Modifier.padding(left = 5)) {
                counter.value--
            }
        }

        val count = withState(counter)
        Text("Counter: $count", modifier = Modifier.padding(left = 5))
    }
}

@Preview(
    name = "Menu 2",
    width = 512,
    height = 380,
)
fun Composable.menuPreview2() {
    val counter = mutableStateOf(0)

    Column(
        modifier = Modifier.fillSize().background(Color.GREEN_12)
    ) {
        Header()

        Row(
            modifier = Modifier
                .padding(5, 3)
        ) {
            TextButton("Increment") {
                counter.value++
            }

            TextButton("Decrement", modifier = Modifier.padding(left = 5)) {
                counter.value--
            }
        }

        val count = withState(counter)
        Text("Counter: $count", modifier = Modifier.padding(left = 5))
    }
}

@Preview
fun Composable.defaultP() {
    val counter = mutableStateOf(0)

    Column(
        modifier = Modifier.fillSize().background(Color.GREEN_12)
    ) {
        Header()

        Row(
            modifier = Modifier
                .padding(5, 3)
        ) {
            TextButton("Increment") {
                counter.value++
            }

            TextButton("Decrement", modifier = Modifier.padding(left = 5)) {
                counter.value--
            }
        }

        val count = withState(counter)
        Text("Counter: $count", modifier = Modifier.padding(left = 5))
    }
}


class TestMenu(player: Player, manager: BukkitMenuManager) : BaseMenu(
    opts = MenuOptions.builder {
        width = 4
        height = 3
        privateFor(player)
    },
    manager = manager,
) {
    private val counter = mutableStateOf(0)

    override fun Composable.getUi() {

        Column(
            modifier = Modifier.fillSize().background(Color.BLUE_3)
        ) {
            Header()

            Row(
                modifier = Modifier
                    .padding(5, 3)
            ) {
                TextButton("Increment") {
                    counter.value++
                }

                TextButton("Decrement", modifier = Modifier.padding(left = 5)) {
                    counter.value--
                }
            }

            val count = withState(counter)
            Text("Counter: $count", modifier = Modifier.padding(left = 5))
        }
    }
}

private fun Composable.Header() {
    Row(
        modifier = Modifier
            .maxSize(height = 44)
            .fillWidth()
    ) {
        HeaderButton("Home")
        HeaderButton("Map")
        HeaderButton("Guild")
        HeaderButton("Settings")
        HeaderCloseButton()
    }
}


private fun Composable.HeaderButton(text: String) {
    TextButton(
        text = text,
        modifier = Modifier
            .weight(1)
            .fillHeight(),
        padding = paddingValuesOf(5),
        colors = ButtonBackgroundColors(
            main = Color.CYAN_3,
            light = Color.CYAN_1,
            dark = Color.CYAN_4,
        )
    )
}

private fun Composable.HeaderCloseButton() {
    Box(
        alignment = Alignment.Center,
        modifier = Modifier
            .border(Color.CYAN_6)
            .background(Color.CYAN_3)
            .clickable {
                // Close the menu
                renderContext.menu.close()
            }
            .padding(5)
    ) {
        Image("icons/32/Close.png")
    }
}
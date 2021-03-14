package com.heroslender.hmf.sample.menu

import com.heroslender.hmf.bukkit.BaseMenu
import com.heroslender.hmf.bukkit.BukkitMenuManager
import com.heroslender.hmf.bukkit.map.Color
import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.font.FontStyle
import com.heroslender.hmf.core.font.MINECRAFTIA_24
import com.heroslender.hmf.core.mutableStateOf
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.components.containers.Box
import com.heroslender.hmf.core.ui.components.containers.Column
import com.heroslender.hmf.core.ui.components.text.Label
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.*
import com.heroslender.hmf.core.ui.withState
import org.bukkit.entity.Player

class TestMenu(player: Player, manager: BukkitMenuManager) : BaseMenu(player, manager = manager) {
    val counter = mutableStateOf(0)

    override fun Composable.getUi() {
        Column(Modifier.fill().backgroundColor(Color.CYAN_4)) {
            NavBar {
                NavIcon("icons/32/dirt.png") {
                    owner.sendMessage("Look, a dirt o.O")
                }

                NavIconEnd("icons/32/Close.png") {
                    destroy()
                    owner.sendMessage("Closed the menu.")
                }
            }

            Box(Modifier.fill()) {
                Square(Color.RED_1, posX = 15, posY = 15) { counter.value-- }
                Square(Color.GREEN_10, posX = 35, posY = 35) { counter.value++ }

                Column(modifier = Modifier.margin(top = 70, left = 15)) {
                    val fontStyle =
                        FontStyle(font = MINECRAFTIA_24, Color.BLACK_1, Color.TRANSPARENT, Color.CYAN_8)
                    val count = withState(counter)
                    Label("Counter: $count", style = fontStyle)
                }
            }
        }
    }

    fun Composable.Square(color: IColor, posX: Int, posY: Int, click: ClickListener) {
        Box(
            modifier = Modifier.fixedSize(25).margin(top = posY, left = posX)
                .backgroundColor(color)
                .clickable(click)
        ) {}
    }
}
package com.heroslender.hmf.bukkit.models

import com.heroslender.hmf.bukkit.BaseMenu
import org.bukkit.entity.Player

class User(
    val player: Player,
) {

    var menu: BaseMenu? = null
}

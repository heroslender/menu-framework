package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.map.MapIcon

data class MenuOptions(
    val cursor: CursorOptions = CursorOptions(),
    val maxInteractDistance: Double = 5.0,
) {
    data class CursorOptions(
        val updateDelay: Long = 2,
        val iconType: MapIcon.Type = MapIcon.Type.GREEN_POINTER,
        val iconRotation: Byte = 6,
    )
}

package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.sdk.map.MapIcon

data class MenuOptions(
    val cursor: CursorOptions = CursorOptions(),
) {
    data class CursorOptions(
        val iconType: MapIcon.Type = MapIcon.Type.GREEN_POINTER,
        val iconRotation: Byte = 6,
    )
}

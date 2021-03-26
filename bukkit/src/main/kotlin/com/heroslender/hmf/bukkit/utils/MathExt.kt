package com.heroslender.hmf.bukkit.utils


inline fun clamp(value: Int, low: Int, high: Int): Int = when {
    value < low -> low
    value > high -> high
    else -> value
}

inline fun clampByte(value: Int, low: Byte = Byte.MIN_VALUE, high: Byte = Byte.MAX_VALUE): Byte = when {
    value < low -> low
    value > high -> high
    else -> value.toByte()
}



package com.heroslender.hmf.bukkit.sdk.nms.version

object IllegalServerException : IllegalStateException("Is this not CraftBukkit?")

@Suppress("MemberVisibilityCanBePrivate")
class UnsupportedServerVersionException(val version: NMSVersion) : IllegalStateException(
    "$version is not a supported version! Please create an issue if you feel like it should be!"
)

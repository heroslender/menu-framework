package com.heroslender.hmf.bukkit.sdk.nms.version

import org.bukkit.Bukkit
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
enum class ServerVersion(val id: String, val displayName: String) {
    V1_8_R3("v1_8_R3", "1.8.8"),
    V1_12_R1("v1_12_R1", "1.12.2");

    val nmsVersion: NMSVersion = NMSVersion.fromString(id)

    companion object {
        val CURRENT: ServerVersion by lazy { current() }

        private val currentVersion: NMSVersion by lazy { NMSVersion.fromString(currentVersionString) }
        private val currentVersionString: String by lazy {
            val name = Bukkit.getServer().javaClass.name
            val parts = name.split(".").toTypedArray()

            return@lazy if (parts.size > 3) {
                parts[3]
            } else "" // We're not on CraftBukkit?!?
        }

        /**
         * Get the current [ServerVersion].
         *
         * @throws UnsupportedServerVersionException The server is running an unsupported version.
         * @throws IllegalServerException The server is not running a CraftBukkit based software,
         */
        fun current(): ServerVersion = try {
            if (currentVersionString == "") {
                throw IllegalServerException
            }

            valueOf(currentVersionString.toUpperCase())
        } catch (e: IllegalArgumentException) {
            throw UnsupportedServerVersionException(currentVersion)
        }
    }
}
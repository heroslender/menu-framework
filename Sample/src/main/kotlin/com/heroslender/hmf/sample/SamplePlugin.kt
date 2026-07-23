package com.heroslender.hmf.sample

import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
import com.heroslender.hmf.bukkit.manager.impl.BukkitMenuManagerImpl
import com.heroslender.hmf.bukkit.sdk.nms.PacketAdapter
import com.heroslender.hmf.bukkit.sdk.nms.version.IllegalServerException
import com.heroslender.hmf.bukkit.sdk.nms.version.ServerVersion
import com.heroslender.hmf.bukkit.sdk.nms.version.UnsupportedServerVersionException
import com.heroslender.hmf.core.font.loadFonts
import com.heroslender.hmf.sample.menu.TestMenu
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class SamplePlugin : JavaPlugin() {
    lateinit var manager: BukkitMenuManager

    override fun onEnable() {
        if (!canRun()) return

        manager = BukkitMenuManagerImpl(this)
        // Load fonts to memory
        loadFonts()

        val adapter = PacketAdapter.current()
        logger.info("Using adapter: ${adapter.javaClass.simpleName}")

        server.getPluginCommand("menu").setExecutor { sender, command, label, args ->
            if (sender !is Player) {
                sender.sendMessage("Only players can open menus.")
                return@setExecutor true
            }

            sender.sendMessage("Opening the menu.")
            TestMenu(sender, manager).send()
            return@setExecutor true
        }
    }

    /**
     * Checks if we can run on this server.
     *
     * PacketEvents (if present) handles version abstraction for us — no need
     * to match against a hardcoded list of server versions. Only fall through
     * to [ServerVersion.current] when PacketEvents is absent and we need a
     * per-version NMS adapter.
     */
    private fun canRun(): Boolean {
        // PacketEvents on the classpath → all versions are supported
        if (isPacketEventsAvailable()) return true

        // No PacketEvents → fall back to per-version NMS support
        return try {
            ServerVersion.current()
            true
        } catch (e: IllegalServerException) {
            logger.severe("Is this not CraftBukkit?!?")
            server.pluginManager.disablePlugin(this)
            false
        } catch (e: UnsupportedServerVersionException) {
            logger.severe("This server version(${e.version}) is not supported by the plugin!")
            server.pluginManager.disablePlugin(this)
            false
        }
    }

    private fun isPacketEventsAvailable(): Boolean {
        return try {
            Class.forName("com.github.retrooper.packetevents.PacketEvents")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }
}

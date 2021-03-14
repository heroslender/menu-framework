package com.heroslender.hmf.sample

import com.heroslender.hmf.bukkit.BukkitMenuManager
import com.heroslender.hmf.bukkit.nms.version.IllegalServerException
import com.heroslender.hmf.bukkit.nms.version.ServerVersion
import com.heroslender.hmf.bukkit.nms.version.UnsupportedServerVersionException
import com.heroslender.hmf.core.font.loadFonts
import com.heroslender.hmf.sample.menu.TestMenu
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class SamplePlugin : JavaPlugin() {
    lateinit var manager: BukkitMenuManager

    override fun onEnable() {
        if (!canRun()) return

        manager = BukkitMenuManager(this)
        // Load fonts to memory
        loadFonts()

        logger.info("Using adapters for ${ServerVersion.CURRENT.displayName}(${ServerVersion.CURRENT.id})")

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
     * Checks if the server version is supported by HMF
     */
    private fun canRun(): Boolean = try {
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
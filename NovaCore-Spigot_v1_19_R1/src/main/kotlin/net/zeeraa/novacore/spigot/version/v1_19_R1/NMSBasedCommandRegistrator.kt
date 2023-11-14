package net.zeeraa.novacore.spigot.version.v1_19_R1

import net.zeeraa.novacore.spigot.abstraction.CommandRegistrator
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.command.CraftCommandMap
import org.bukkit.plugin.Plugin

/**
 * Based on this:
 * https://bukkit.org/threads/register-command-without-plugin-yml.112932/#post-1430463
 *
 * @author Zeeraa
 */
class NMSBasedCommandRegistrator : CommandRegistrator {
	private var commandMap: CommandMap? = null

	init {
		try {
			val f = CraftServer::class.java.getDeclaredField("commandMap")
			f.isAccessible = true
			commandMap = f[Bukkit.getServer()] as CommandMap
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun registerCommand(plugin: Plugin, command: Command) {
		commandMap!!.register(plugin.name, command)
	}

	override fun getCommandMap(): CommandMap? {
		return commandMap
	}

	override fun syncCommands(): Boolean {
		val server = Bukkit.getServer() as CraftServer
		server.syncCommands()
		return true
	}

	override fun tryGetKnownCommandsFromSimpleCommandMap(commandMap: SimpleCommandMap): Map<String, Command>? {
		if (commandMap is CraftCommandMap) {
			return commandMap.knownCommands
		}
		return null
	}
}
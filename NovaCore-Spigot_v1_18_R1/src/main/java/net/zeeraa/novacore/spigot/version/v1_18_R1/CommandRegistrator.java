package net.zeeraa.novacore.spigot.version.v1_18_R1;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;

/**
 * Based on this:
 * https://bukkit.org/threads/register-command-without-plugin-yml.112932/#post-1430463
 * 
 * @author Zeeraa
 */
public class CommandRegistrator implements net.zeeraa.novacore.spigot.abstraction.CommandRegistrator {
	private CommandMap cmap;

	public CommandRegistrator() {
		try {
			if (Bukkit.getServer() instanceof CraftServer) {
				Field f = CraftServer.class.getDeclaredField("commandMap");
				f.setAccessible(true);
				cmap = (CommandMap) f.get(Bukkit.getServer());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void registerCommand(Command command) {
		cmap.register("", command);
	}

	@Override
	public CommandMap getCommandMap() {
		return cmap;
	}
}
package net.zeeraa.novacore.spigot.version.v1_19_R3.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.PlayerConnection;
import net.zeeraa.novacore.spigot.abstraction.log.AbstractionLogger;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketManager extends net.zeeraa.novacore.spigot.abstraction.packet.PacketManager {
	public void registerPlayer(Player player) {
		NetworkManager networkManager;
		try {
			networkManager = getNetworkManager(player);
			MinecraftChannelDuplexHandler channelDuplexHandler = new MinecraftChannelDuplexHandler(player);
			ChannelPipeline pipeline = networkManager.m.pipeline();
			pipeline.addBefore("packet_handler", player.getUniqueId().toString(), channelDuplexHandler);
			AbstractionLogger.getLogger().trace("PacketManager", "Injected listener to " + player.getName());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			AbstractionLogger.getLogger().error("PacketManager", "Failed to get NetworkManager instance from player " + player.getName() + ". " + e.getClass().getName() + " " + e.getMessage());
		}
	}

	@Override
	public void removePlayer(Player player) {
		try {
			NetworkManager networkManager = getNetworkManager(player);
			Channel channel = networkManager.m;
			channel.eventLoop().submit(() -> {
				channel.pipeline().remove(player.getUniqueId().toString());
				return null;
			});
			AbstractionLogger.getLogger().trace("PacketManager", "Removed listener from " + player.getName());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			AbstractionLogger.getLogger().error("PacketManager", "Failed to get NetworkManager instance from player " + player.getName() + ". " + e.getClass().getName() + " " + e.getMessage());
		}
	}

	public static NetworkManager getNetworkManager(Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
		Field field = connection.getClass().getField("h");
		field.setAccessible(true);
		return (NetworkManager) field.get(connection);
	}
}
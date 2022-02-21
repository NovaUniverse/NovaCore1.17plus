package net.zeeraa.novacore.spigot.version.v1_17_R1;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar implements net.zeeraa.novacore.spigot.abstraction.ActionBar {
	@Override
	public void sendMessage(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}
}
package net.zeeraa.novacore.spigot.version.v1_17_R1;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependantPlayerAchievementAwardedEvent;

public class Listeners extends net.zeeraa.novacore.spigot.abstraction.Listeners implements Listener {
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onAchievement(PlayerAdvancementDoneEvent e) {
		VersionIndependantPlayerAchievementAwardedEvent event = new VersionIndependantPlayerAchievementAwardedEvent(e.getPlayer(), e.getAdvancement().getKey().toString(), false);

		Bukkit.getServer().getPluginManager().callEvent(event);
	}
}
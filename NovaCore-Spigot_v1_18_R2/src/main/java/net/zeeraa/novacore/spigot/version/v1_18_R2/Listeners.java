package net.zeeraa.novacore.spigot.version.v1_18_R2;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependantPlayerAchievementAwardedEvent;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependantPlayerPickUpItemEvent;

public class Listeners extends net.zeeraa.novacore.spigot.abstraction.Listeners implements Listener {
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onAchievement(PlayerAdvancementDoneEvent e) {
		VersionIndependantPlayerAchievementAwardedEvent event = new VersionIndependantPlayerAchievementAwardedEvent(e.getPlayer(), e.getAdvancement().getKey().toString(), false);

		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityPickupItem(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player) {
			VersionIndependantPlayerPickUpItemEvent event = new VersionIndependantPlayerPickUpItemEvent((Player) e.getEntity(), e.getItem());
			event.setCancelled(e.isCancelled());

			Bukkit.getServer().getPluginManager().callEvent(event);
			
			e.setCancelled(event.isCancelled());
		}
	}
}
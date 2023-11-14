package net.zeeraa.novacore.spigot.version.v1_20_R2

import net.zeeraa.novacore.spigot.abstraction.Listeners
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerAchievementAwardedEvent
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerPickUpItemEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class ListenersImplementation : Listeners(), Listener {
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) fun onAchievement(e: PlayerAdvancementDoneEvent) {
		val event = VersionIndependentPlayerAchievementAwardedEvent(e.player, e.advancement.key.toString(), false)
		Bukkit.getServer().pluginManager.callEvent(event)
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) fun onEntityPickupItem(e: EntityPickupItemEvent) {
		if (e.entity is Player) {
			val event = VersionIndependentPlayerPickUpItemEvent(e.entity as Player, e.item)
			event.isCancelled = e.isCancelled
			Bukkit.getServer().pluginManager.callEvent(event)
			e.isCancelled = event.isCancelled
		}
	}
}
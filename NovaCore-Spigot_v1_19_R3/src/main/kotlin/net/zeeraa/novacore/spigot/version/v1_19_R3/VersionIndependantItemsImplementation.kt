package net.zeeraa.novacore.spigot.version.v1_19_R3

import net.zeeraa.novacore.spigot.abstraction.VersionIndependentItems
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class VersionIndependantItemsImplementation : VersionIndependentItems() {
	override fun getPlayerSkull(): ItemStack {
		return ItemStack(Material.PLAYER_HEAD, 1)
	}

	override fun isPlayerSkull(item: ItemStack): Boolean {
		return item.type == Material.PLAYER_HEAD || item.type == Material.PLAYER_WALL_HEAD
	}
}
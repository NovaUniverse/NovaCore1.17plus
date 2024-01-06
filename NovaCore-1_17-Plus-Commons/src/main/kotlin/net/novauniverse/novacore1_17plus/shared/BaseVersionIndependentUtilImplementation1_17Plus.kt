package net.novauniverse.novacore1_17plus.shared

import net.novauniverse.spigot.version.shared.v1_16plus.BaseVersionIndependentUtilImplementation1_16Plus
import net.novauniverse.spigot.version.shared.v1_16plus.DyeColorToMaterialMapper
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentLoader
import org.bukkit.entity.Player

abstract class BaseVersionIndependentUtilImplementation1_17Plus(loader: VersionIndependentLoader, colorToMaterialMapper: DyeColorToMaterialMapper) : BaseVersionIndependentUtilImplementation1_16Plus(loader, colorToMaterialMapper) {
	override fun getPlayerPing(player: Player): Int {
		return player.ping
	}

	override fun sendTabList(player: Player, header: String, footer: String) {
		player.setPlayerListHeaderFooter(header, footer)
	}
}
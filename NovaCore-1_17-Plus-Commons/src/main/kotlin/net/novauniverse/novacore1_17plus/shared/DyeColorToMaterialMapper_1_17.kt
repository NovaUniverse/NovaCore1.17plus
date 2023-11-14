package net.novauniverse.novacore1_17plus.shared

import net.novauniverse.spigot.version.shared.v1_16plus.DyeColorToMaterialMapper
import org.bukkit.DyeColor
import org.bukkit.Material

class DyeColorToMaterialMapper_1_17 : DyeColorToMaterialMapper {
	override fun dyeColorToMaterial(color: DyeColor): Material? {
		return when (color) {
			DyeColor.BLACK -> Material.INK_SAC
			DyeColor.BLUE -> Material.LAPIS_LAZULI
			DyeColor.BROWN -> Material.COCOA_BEANS
			DyeColor.CYAN -> Material.CYAN_DYE
			DyeColor.GRAY -> Material.GRAY_DYE
			DyeColor.GREEN -> Material.GREEN_DYE
			DyeColor.LIGHT_BLUE -> Material.LIGHT_BLUE_DYE
			DyeColor.LIGHT_GRAY -> Material.LIGHT_GRAY_DYE
			DyeColor.LIME -> Material.LIME_DYE
			DyeColor.MAGENTA -> Material.MAGENTA_DYE
			DyeColor.ORANGE -> Material.ORANGE_DYE
			DyeColor.PINK -> Material.PINK_DYE
			DyeColor.PURPLE -> Material.PURPLE_DYE
			DyeColor.RED -> Material.RED_DYE
			DyeColor.WHITE -> Material.WHITE_DYE
			DyeColor.YELLOW -> Material.YELLOW_DYE
			else -> null
		}
	}
}
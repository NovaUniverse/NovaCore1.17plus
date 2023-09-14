package net.novauniverse.novacore1_17plus.shared;

import org.bukkit.entity.Player;

import net.novauniverse.spigot.version.shared.v1_16plus.BaseVersionIndependentUtilImplementation1_16Plus;
import net.novauniverse.spigot.version.shared.v1_16plus.DyeColorToMaterialMapper;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantLoader;

public abstract class BaseVersionIndependentUtilImplementation1_17Plus extends BaseVersionIndependentUtilImplementation1_16Plus {
	public BaseVersionIndependentUtilImplementation1_17Plus(VersionIndependantLoader loader, DyeColorToMaterialMapper colorToMaterialMapper) {
		super(loader, colorToMaterialMapper);
	}

	@Override
	public int getPlayerPing(Player player) {
		return player.getPing();
	}

	@Override
	public void sendTabList(Player player, String header, String footer) {
		player.setPlayerListHeaderFooter(header, footer);
	}
}
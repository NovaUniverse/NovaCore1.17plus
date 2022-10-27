package net.zeeraa.novacore.spigot.version.v1_18_R1.packet;

import net.minecraft.network.protocol.game.PacketPlayInArmAnimation;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInSettings;
import net.minecraft.network.protocol.game.PacketPlayInSpectate;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ChatVisibility;
import net.zeeraa.novacore.spigot.abstraction.enums.Hand;
import net.zeeraa.novacore.spigot.abstraction.enums.MainHand;
import net.zeeraa.novacore.spigot.abstraction.packet.event.PlayerAttemptBreakBlockEvent;
import net.zeeraa.novacore.spigot.abstraction.packet.event.PlayerSettingsEvent;
import net.zeeraa.novacore.spigot.abstraction.packet.event.PlayerSwingEvent;
import net.zeeraa.novacore.spigot.abstraction.packet.event.SpectatorTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.util.*;

public class MinecraftChannelDuplexHandler extends net.zeeraa.novacore.spigot.abstraction.packet.MinecraftChannelDuplexHandler {

	public MinecraftChannelDuplexHandler(Player player) {
		super(player);
	}

	public boolean readPacket(Player player, Object packet) throws NoSuchFieldException, IllegalAccessException {
		List<Player> playersDigging = VersionIndependentUtils.get().getPacketManager().getPlayersDigging();
		Set<Material> transparent = new HashSet<>();
		transparent.add(Material.AIR);
		transparent.add(Material.WATER);
		transparent.add(Material.LAVA);
		List<Event> events = new ArrayList<>();
		if (packet.getClass().equals(PacketPlayInSettings.class)) {
			PacketPlayInSettings settings = (PacketPlayInSettings) packet;

			events.add(new PlayerSettingsEvent(player, settings.b, settings.c, ChatVisibility.fromNMS(settings.d().name()), settings.e(), settings.f(), MainHand.valueOfNMS(settings.g().name()), settings.h(), settings.i()));

		} else if (packet.getClass().equals(PacketPlayInArmAnimation.class)) {
			PacketPlayInArmAnimation arm = (PacketPlayInArmAnimation) packet;

			events.add(new PlayerSwingEvent(player, System.currentTimeMillis(), Hand.valueOf(arm.b().name())));

			if (canBreak(player, player.getTargetBlock(transparent, 5))) {
				events.add(new PlayerAttemptBreakBlockEvent(player, System.currentTimeMillis(), player.getTargetBlock(transparent, 5)));
			}
		} else if (packet.getClass().equals(PacketPlayInSpectate.class)) {
			PacketPlayInSpectate spectate = (PacketPlayInSpectate) packet;
			Field field = PacketPlayInSpectate.class.getDeclaredField("a");
			field.setAccessible(true);
			UUID id = (UUID) field.get(spectate);
			events.add(new SpectatorTeleportEvent(player, Bukkit.getPlayer(id)));

		} else if (packet.getClass().equals(PacketPlayInBlockDig.class)) {
			PacketPlayInBlockDig action = (PacketPlayInBlockDig) packet;

			switch (action.d()) {
			case a:
				if (playersDigging.stream().noneMatch(pl -> pl.getUniqueId().equals(player.getUniqueId()))) {
					playersDigging.add(player);
					if (canBreak(player, player.getTargetBlock(transparent, 5))) {
						events.add(new PlayerAttemptBreakBlockEvent(player, System.currentTimeMillis(), player.getTargetBlock(transparent, 5)));
					}
				}
				break;
			case b:
			case c:
				playersDigging.remove(player);
				break;
			}
		}
		if (events.isEmpty())
			return true;

		boolean value = true;
		for (Event event : events) {
			Bukkit.getPluginManager().callEvent(event);
			if (((Cancellable) event).isCancelled()) {
				value = false;
				break;
			}
		}
		return value;
	}
}

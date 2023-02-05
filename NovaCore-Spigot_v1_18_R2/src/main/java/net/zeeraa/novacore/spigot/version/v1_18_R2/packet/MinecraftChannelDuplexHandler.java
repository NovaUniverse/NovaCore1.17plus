package net.zeeraa.novacore.spigot.version.v1_18_R2.packet;

import net.minecraft.network.protocol.game.PacketPlayInArmAnimation;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInSettings;
import net.minecraft.network.protocol.game.PacketPlayInSpectate;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ChatVisibility;
import net.zeeraa.novacore.spigot.abstraction.enums.Hand;
import net.zeeraa.novacore.spigot.abstraction.enums.MainHand;
import net.zeeraa.novacore.spigot.abstraction.enums.SoundCategory;
import net.zeeraa.novacore.spigot.abstraction.packet.event.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MinecraftChannelDuplexHandler extends net.zeeraa.novacore.spigot.abstraction.packet.MinecraftChannelDuplexHandler {

	public MinecraftChannelDuplexHandler(Player player) {
		super(player);
	}

	public boolean readPacket(Player player, Object packet) throws NoSuchFieldException, IllegalAccessException {
		List<Event> events = new ArrayList<>();
		if (packet.getClass().equals(PacketPlayInSettings.class)) {
			PacketPlayInSettings settings = (PacketPlayInSettings) packet;
			events.add(new PlayerSettingsEvent(player, settings.b, settings.c, ChatVisibility.fromNMS(settings.d().name()), settings.e(), settings.f(), MainHand.valueOfNMS(settings.g().name()), settings.h(), settings.i()));

		} else if (packet.getClass().equals(PacketPlayInArmAnimation.class)) {
			PacketPlayInArmAnimation arm = (PacketPlayInArmAnimation) packet;

			events.add(new PlayerSwingEvent(player, System.currentTimeMillis(), Hand.valueOf(arm.b().name())));

			if (canBreak(player, VersionIndependentUtils.get().getReacheableBlockExact(player))) {
				events.add(new PlayerAttemptBreakBlockEvent(player, System.currentTimeMillis(), VersionIndependentUtils.get().getReacheableBlockExact(player)));
			}
		} else if (packet.getClass().equals(PacketPlayInSpectate.class)) {
			PacketPlayInSpectate spectate = (PacketPlayInSpectate) packet;
			Field field = PacketPlayInSpectate.class.getDeclaredField("a");
			field.setAccessible(true);
			UUID id = (UUID) field.get(spectate);
			events.add(new SpectatorTeleportEvent(player, Bukkit.getPlayer(id)));

		} else if (packet.getClass().equals(PacketPlayInBlockDig.class)) {
			List<Player> playersDigging = VersionIndependentUtils.get().getPacketManager().getPlayersDigging();
			PacketPlayInBlockDig action = (PacketPlayInBlockDig) packet;

			switch (action.d()) {
			case a:
				if (playersDigging.stream().noneMatch(pl -> pl.getUniqueId().equals(player.getUniqueId()))) {
					playersDigging.add(player);
					Block block = VersionIndependentUtils.get().getReacheableBlockExact(player);
					if (block != null) {
						if (canBreak(player, block)) {
							events.add(new PlayerAttemptBreakBlockEvent(player, System.currentTimeMillis(), block));
						}
					}
				}
				break;
			case b:
			case c:
				playersDigging.remove(player);
				break;

			default:
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

	@Override
	public boolean writePacket(Player player, Object packet) {
		List<Event> events = new ArrayList<>();
		if (packet.getClass().equals(PacketPlayOutNamedSoundEffect.class)) {
			PacketPlayOutNamedSoundEffect effect = (PacketPlayOutNamedSoundEffect) packet;

			SoundCategory soundCategory = Arrays.stream(SoundCategory.values()).filter(sc -> sc.getName().equalsIgnoreCase(effect.c().a())).findFirst().get();
			SoundEffect effect1 = effect.b();
			MinecraftKey mcKey = effect1.a();
			Sound foundSound = Arrays.stream(Sound.values()).filter(sound -> sound.getKey().toString().equalsIgnoreCase(mcKey.toString())).findFirst().get();
			net.zeeraa.novacore.spigot.abstraction.enums.SoundCategory category = Arrays.stream(SoundCategory.values()).filter(sc -> sc.getName().equalsIgnoreCase(soundCategory.getName())).findFirst().get();

			double x = effect.d();
			double y = effect.e();
			double z = effect.f();
			float volume = effect.g();
			float pitch = effect.h();

			events.add(new PlayerListenSoundEvent(player, foundSound, category, x, y, z, volume, pitch));
		}

		for (Event e : events) {
			Bukkit.getPluginManager().callEvent(e);
			if (((Cancellable) e).isCancelled()) {
				return false;
			}
		}
		return true;
	}
}
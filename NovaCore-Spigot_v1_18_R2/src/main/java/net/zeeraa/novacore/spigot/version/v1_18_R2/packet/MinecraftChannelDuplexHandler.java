package net.zeeraa.novacore.spigot.version.v1_18_R2.packet;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.zeeraa.novacore.spigot.abstraction.packet.PacketManager;
import net.zeeraa.novacore.spigot.abstraction.packet.event.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import java.util.ArrayList;
import java.util.List;

public class MinecraftChannelDuplexHandler extends net.zeeraa.novacore.spigot.abstraction.packet.MinecraftChannelDuplexHandler {

	public MinecraftChannelDuplexHandler(Player player) {
		super(player);
	}

	public boolean readPacket(Player player, Object packet) {
		List<PacketEvent> events = new ArrayList<>();
		if (packet.getClass().equals(PacketPlayInSteerVehicle.class)) {
			PacketPlayInSteerVehicle steer = (PacketPlayInSteerVehicle) packet;
			float sideways = steer.b();
			float forwards = steer.c();
			boolean jump = steer.d();
			boolean leave = steer.e();
			if (sideways != 0 || forwards != 0 || jump || leave) {
				PlayerInputEvent.Press side;
				PlayerInputEvent.Press front;
				if (sideways > 0) {
					side = PlayerInputEvent.Press.LEFT;
				} else if (sideways < 0) {
					side = PlayerInputEvent.Press.RIGHT;
				} else {
					side = PlayerInputEvent.Press.NONE;
				}
				if (forwards > 0) {
					front = PlayerInputEvent.Press.FRONT;
				} else if (forwards < 0) {
					front = PlayerInputEvent.Press.BACK;
				} else {
					front = PlayerInputEvent.Press.NONE;
				}
				events.add(new PlayerInputEvent(player, side, front, jump, leave));
			}
		} else if (packet.getClass().equals(PacketPlayInBlockDig.class)) {
			PacketPlayInBlockDig dig = (PacketPlayInBlockDig) packet;
			BlockPosition bp = dig.b();
			Block block = player.getWorld().getBlockAt(bp.u(), bp.v(), bp.w());
			BlockFace face = switch (dig.c()) {
				case b -> BlockFace.UP;
				case a -> BlockFace.DOWN;
				case f -> BlockFace.EAST;
				case e -> BlockFace.WEST;
				case c -> BlockFace.NORTH;
				case d -> BlockFace.SOUTH;
			};
			switch (dig.d()) {
				case c -> events.add(new PlayerStopBlockDigEvent(player, block, face));
				case b -> events.add(new PlayerAbortBlockDigEvent(player, block, face));
				case a -> events.add(new PlayerStartBlockDigEvent(player, block, face));
				default -> {}
			}
		}

		for (PacketEvent e : events) {
			net.zeeraa.novacore.spigot.abstraction.packet.PacketManager.fireEvent(e);
			if (e instanceof Cancellable) {
				if (((Cancellable) e).isCancelled()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean writePacket(Player player, Object packet) {
		List<PacketEvent> events = new ArrayList<>();

		for (PacketEvent e : events) {
			PacketManager.fireEvent(e);
			if (e instanceof Cancellable) {
				if (((Cancellable) e).isCancelled()) {
					return false;
				}
			}
		}
		return true;
	}
}
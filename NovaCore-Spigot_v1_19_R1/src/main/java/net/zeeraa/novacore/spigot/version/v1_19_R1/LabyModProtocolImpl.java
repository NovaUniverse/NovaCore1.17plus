package net.zeeraa.novacore.spigot.version.v1_19_R1;

import org.bukkit.entity.Player;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import net.zeeraa.novacore.spigot.abstraction.LabyModProtocol;

// No version of LabyMod exist for 1.19 so lets just nuke the code
public class LabyModProtocolImpl extends LabyModProtocol {
	public void sendLabyModMessage(Player player, String key, JsonElement messageContent) {

	}

	public byte[] getBytesToSend(String messageKey, String messageContents) {
		return new byte[0];
	}

	public void writeVarIntToBuffer(ByteBuf buf, int input) {
	}

	public void writeString(ByteBuf buf, String string) {
	}

	public int readVarIntFromBuffer(ByteBuf buf) {
		return 0;
	}

	public String readString(ByteBuf buf, int maxLength) {
		return null;
	}
}
package net.zeeraa.novacore.spigot.version.v1_18_R2;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.server.ScoreboardServer.Action;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;
import net.zeeraa.novacore.commons.utils.ReflectUtils;
import net.zeeraa.novacore.spigot.abstraction.INetheriteBoard;
import net.zeeraa.novacore.spigot.abstraction.enums.ObjectiveMode;
import net.zeeraa.novacore.spigot.abstraction.netheriteboard.BPlayerBoard;

public class PlayerBoardV1_18_R2 extends BPlayerBoard {
	private EntityPlayer playerHandle;
	private Field playerScoreField;

	public PlayerBoardV1_18_R2(INetheriteBoard netheriteBoard, Player player, String name) throws Exception {
		this(netheriteBoard, player, null, name);
	}

	public PlayerBoardV1_18_R2(INetheriteBoard netheriteBoard, Player player, Scoreboard scoreboard, String name) throws Exception {
		super(netheriteBoard, player, scoreboard, name);
		playerHandle = ((CraftPlayer) player).getHandle();

		playerScoreField = net.minecraft.world.scores.Scoreboard.class.getDeclaredField("j");
		playerScoreField.setAccessible(true);
		
		this.init();
	}

	@Override
	protected void sendObjective(Objective objective, ObjectiveMode mode) throws Exception {
		ScoreboardObjective nmsObjective = (ScoreboardObjective) ReflectUtils.getHandle(objective);
		PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(nmsObjective, mode.ordinal());
		playerHandle.b.a(packet);
	}

	@Override
	protected void sendObjectiveDisplay(Objective objective) throws Exception {
		ScoreboardObjective nmsObjective = (ScoreboardObjective) ReflectUtils.getHandle(objective);
		PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective(1, nmsObjective);
		playerHandle.b.a(packet);
	}

	@Override
	protected void sendScore(Objective objective, String name, int score, boolean remove) throws Exception {
		net.minecraft.world.scores.Scoreboard nmsScoreboard = ((CraftScoreboard) this.scoreboard).getHandle();
		ScoreboardObjective nmsObjective = (ScoreboardObjective) ReflectUtils.getHandle(objective);

		ScoreboardScore scoreboardScore = new ScoreboardScore(nmsScoreboard, nmsObjective, name);
		scoreboardScore.b(score);

		@SuppressWarnings("unchecked")
		Map<String, Map<ScoreboardObjective, ScoreboardScore>> scores = (Map<String, Map<ScoreboardObjective, ScoreboardScore>>) playerScoreField.get(nmsScoreboard);

		if (remove) {
			if (scores.containsKey(name)) {
				scores.get(name).remove(nmsObjective);
			}
		} else {
			if (!scores.containsKey(name)) {
				scores.put(name, new HashMap<>());
			}

			scores.get(name).put(nmsObjective, scoreboardScore);
		}

		PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(remove ? Action.a : Action.b, objective.getName(), name, score);

		playerHandle.b.a(packet);
	}
}
package net.zeeraa.novacore.spigot.version.v1_17_R1.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketManager extends net.zeeraa.novacore.spigot.abstraction.packet.PacketManager {

    public void registerPlayer(Player player) {
        MinecraftChannelDuplexHandler channelDuplexHandler = new MinecraftChannelDuplexHandler(player);
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().b.a.k.pipeline();
        pipeline.addBefore("packet_handler", player.getUniqueId().toString(), channelDuplexHandler);
    }

    @Override
    public void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().b.a.k;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getUniqueId().toString());
            return null;
        });
    }

}

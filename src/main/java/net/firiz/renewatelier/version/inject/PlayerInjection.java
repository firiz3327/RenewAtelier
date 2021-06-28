package net.firiz.renewatelier.version.inject;

import io.netty.channel.Channel;
import net.firiz.ateliercommonapi.nms.MinecraftConverter;
import net.minecraft.network.NetworkManager;
import org.bukkit.entity.Player;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerInjection {

    private static final String KEY_HANDLER = "packet_handler";
    private static final String KEY_PLAYER = "packet_listener_player";
    private static final String KEY_SERVER = "packet_listener_server";
    private static final Executor addChannelExecutor = Executors.newSingleThreadExecutor();

    public static void inject(Player player) {
        injectPacketPipeline(player);
    }

    private static void injectPacketPipeline(Player player) {
        removePacketPipeline(player);
        final NetworkManager networkManager = MinecraftConverter.connection(player).a();

        // netty
        final Channel channel = networkManager.k;
        addChannelExecutor.execute(() -> {
            try {
                channel.pipeline().addBefore(KEY_HANDLER, KEY_PLAYER, new ChannelHandler(MinecraftConverter.convertNMS(player)));
            } catch (Exception var2) {
                throw new RuntimeException(var2);
            }
        });
    }

    private static void removePacketPipeline(Player player) {
        final NetworkManager networkManager = MinecraftConverter.connection(player).a();
        final Channel channel = networkManager.k;
        if (channel.pipeline().get(ChannelHandler.class) != null) {
            channel.pipeline().remove(ChannelHandler.class);
        }
    }

}

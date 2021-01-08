package net.firiz.renewatelier.version.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.constants.ServerConstants;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author firiz
 */
public class PayloadPacket {

    private PayloadPacket() {
    }

    @MinecraftVersion("1.16")
    private static final String CHANNEL_BRAND = "minecraft:brand";

    static {
        final StandardMessenger messenger = (StandardMessenger) Bukkit.getMessenger();
        try {
            @MinecraftVersion("1.16") final Method method = StandardMessenger.class.getDeclaredMethod(
                    "addToOutgoing",
                    Plugin.class,
                    String.class
            );
            method.setAccessible(true);
            method.invoke(messenger, AtelierPlugin.getPlugin(), CHANNEL_BRAND);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            CommonUtils.logWarning(ex);
        }
    }

    @MinecraftVersion("1.16")
    private static void addChannel(final Player player) {
        try {
            final Field playerChannels = CraftPlayer.class.getDeclaredField("channels");
            playerChannels.setAccessible(true);
            final Set<String> channels = CommonUtils.cast(playerChannels.get(player));
            channels.add(CHANNEL_BRAND);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            CommonUtils.logWarning(ex);
        }
    }

    @MinecraftVersion("1.16")
    public static void sendBrand(final Player player) {
        addChannel(player);

        final String brand = ServerConstants.SERVER_NAME + " (ping: " + PacketUtils.getPing(player) + ")" + ChatColor.RESET;
        final ByteBuf buf = Unpooled.buffer();
        final byte[] utf8Bytes = brand.getBytes(StandardCharsets.UTF_8);
        buf.writeByte(utf8Bytes.length & 0x7F);
        buf.writeBytes(utf8Bytes);
        final byte[] result = buf.array();
        buf.release();
        player.sendPluginMessage(AtelierPlugin.getPlugin(), CHANNEL_BRAND, result);
    }

}

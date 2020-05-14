package net.firiz.renewatelier.version.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.utils.Chore;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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

    private static final String CHANNEL_BRAND = "minecraft:brand";

    static {
        final StandardMessenger messenger = (StandardMessenger) Bukkit.getMessenger();
        try {
            final Method method = StandardMessenger.class.getDeclaredMethod("addToOutgoing", Plugin.class, String.class);
            method.setAccessible(true);
            method.invoke(messenger, AtelierPlugin.getPlugin(), CHANNEL_BRAND);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            Chore.logWarning(ex);
        }
    }

    public static void openBook(final Player player, final EquipmentSlot hand) {
        PacketUtils.sendPacket(
                player,
                new PacketPlayOutOpenBook(
                        hand == EquipmentSlot.HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND
                )
        );
    }

    private static void addChannel(final Player player) {
        try {
            final Field playerChannels = CraftPlayer.class.getDeclaredField("channels");
            playerChannels.setAccessible(true);
            final Set<String> channels = Chore.cast(playerChannels.get(player));
            channels.add(CHANNEL_BRAND);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Chore.logWarning(ex);
        }
    }

    public static void sendBrand(final Player player) {
        addChannel(player);

        final String brand = ChatColor.GREEN + "Atelier (ping: " + PacketUtils.getPing(player) + ")" + ChatColor.RESET;
        final ByteBuf buf = Unpooled.buffer();
        final byte[] utf8Bytes = brand.getBytes(StandardCharsets.UTF_8);
        buf.writeByte(utf8Bytes.length & 0x7F);
        buf.writeBytes(utf8Bytes);
        final byte[] result = buf.array();
        buf.release();
        player.sendPluginMessage(AtelierPlugin.getPlugin(), CHANNEL_BRAND, result);
    }

}

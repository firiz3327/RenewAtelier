package net.firiz.renewatelier.version.packet;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtils {

    private PacketUtils() {
    }

    public static void sendPacket(final Player player, final Packet<?> packet) {
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(packet);
    }

    public static void sendPackets(final Player player, final Packet<?>... packets) {
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        for (final Packet<?> packet : packets) {
            playerConnection.sendPacket(packet);
        }
    }

    public static void sendPackets(final PlayerConnection playerConnection, final Packet<?>... packets) {
        for (final Packet<?> packet : packets) {
            playerConnection.sendPacket(packet);
        }
    }

    public static int getPing(final Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

}

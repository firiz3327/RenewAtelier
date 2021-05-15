package net.firiz.renewatelier.version.packet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class PacketUtils {

    private PacketUtils() {
    }

    public static void sendPacketWorld(final org.bukkit.World world, final org.bukkit.entity.Entity entity, final Packet<?> packet) {
        ((CraftWorld) world).getHandle().getChunkProvider().broadcast(((CraftEntity) entity).getHandle(), packet);
    }

    public static void sendPacketWorld(final World world, final org.bukkit.entity.Entity entity, final Packet<?> packet) {
        ((WorldServer) world).getChunkProvider().broadcast(((CraftEntity) entity).getHandle(), packet);
    }

    public static void sendPacketWorld(final World world, final Entity entity, final Packet<?> packet) {
        ((WorldServer) world).getChunkProvider().broadcast(entity, packet);
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

    public static void sendPackets(final Player player, final Collection<Packet<?>> packets) {
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        for (final Packet<?> packet : packets) {
            playerConnection.sendPacket(packet);
        }
    }

    public static void broadcast(final org.bukkit.entity.Entity entity, Packet<?> packet) {
        final Entity nmsEntity = ((CraftEntity) entity).getHandle();
        ((WorldServer) nmsEntity.world).getChunkProvider().broadcast(nmsEntity, packet);
    }

    public static Collection<Player> trackPlayer(final org.bukkit.entity.Entity entity) {
        final Entity nmsEntity = ((CraftEntity) entity).getHandle();
        final PlayerChunkMap.EntityTracker entityTracker = ((WorldServer) nmsEntity.world).getChunkProvider().playerChunkMap.trackedEntities.get(nmsEntity.getId());
        return new ObjectOpenHashSet<>(entityTracker.trackedPlayers)
                .stream()
                .map(entityPlayer -> (Player) entityPlayer.getBukkitEntity())
                .collect(Collectors.toCollection(ObjectOpenHashSet::new));
    }

    public static int getPing(final Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

}

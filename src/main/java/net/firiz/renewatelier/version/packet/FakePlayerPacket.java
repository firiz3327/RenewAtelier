package net.firiz.renewatelier.version.packet;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.version.nms.VEntityPlayer;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public class FakePlayerPacket {

    private FakePlayerPacket() {
    }

    public static VEntityPlayer createEntityPlayer(final World world, final Location location, final UUID uuid, final String name) {
        final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        final WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        final GameProfile profile = new GameProfile(uuid, name);
        final EntityPlayer entityPlayer = new EntityPlayer(server, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
        entityPlayer.listName = new ChatMessage("");
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return new VEntityPlayer(entityPlayer, entityPlayer.getId(), uuid, name, location);
    }

    private static PacketPlayOutPlayerInfo getInfo(final List<VEntityPlayer> players, final boolean remove) {
        final List<EntityPlayer> eps = new ObjectArrayList<>();
        players.forEach(vEps -> eps.add((EntityPlayer) vEps.getEntityPlayer()));
        return new PacketPlayOutPlayerInfo(
                remove
                        ? PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER
                        : PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                eps
        );
    }

    public static void sendPlayer(final Player player, final List<VEntityPlayer> players, final boolean remove) {
        PacketUtils.sendPackets(player, getInfo(players, remove));
    }

    public static void sendLogout(final Player player, final List<VEntityPlayer> players) {
        final int[] ids = new int[players.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = players.get(i).getId();
        }
        PacketUtils.sendPackets(
                player,
                getInfo(players, true),
                new PacketPlayOutEntityDestroy(ids)
        );
    }

    public static void sendSkin(final Player player, final VEntityPlayer vePlayer, final byte bitmask) {
        final EntityPlayer ePlayer = (EntityPlayer) vePlayer.getEntityPlayer();
        final DataWatcher watcher = ePlayer.getDataWatcher();
        watcher.set(DataWatcherRegistry.a.a(15), bitmask); // 1.13.2(13) -> 1.14(15)
        PacketUtils.sendPackets(
                player,
                new PacketPlayOutEntityMetadata(
                        ePlayer.getId(),
                        watcher,
                        true
                ),
                new PacketPlayOutNamedEntitySpawn(ePlayer)
        );
    }

}

package net.firiz.renewatelier.version.packet;

import com.mojang.authlib.GameProfile;

import java.util.List;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.utils.FakeId;
import net.firiz.renewatelier.version.MinecraftVersion;
import net.firiz.renewatelier.version.nms.VEntityPlayer;
import net.minecraft.server.v1_16_R1.DataWatcher;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.MinecraftServer;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R1.PlayerInteractManager;
import net.minecraft.server.v1_16_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public class FakePlayerPacket {

    private FakePlayerPacket() {
    }

    @MinecraftVersion("1.16")
    public static VEntityPlayer createEntityPlayer(final World world, final Location location, final UUID uuid, final String name) {
        final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        final WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        final GameProfile profile = new GameProfile(uuid, name);
        final EntityPlayer entityPlayer = new EntityPlayer(server, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
        final int fakeId = FakeId.createId();
        entityPlayer.e(fakeId); // Entity.id = fakeId (1.16)
        entityPlayer.listName = CraftChatMessage.fromStringOrNull("npc");
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return new VEntityPlayer(entityPlayer, fakeId, uuid, name, location);
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
            ids[i] = players.get(i).getEntityId();
        }
        PacketUtils.sendPackets(
                player,
                getInfo(players, true),
                new PacketPlayOutEntityDestroy(ids)
        );
    }

    @MinecraftVersion("1.16")
    public static void sendSkin(final Player player, final VEntityPlayer vePlayer, final byte bitmask) {
        final EntityPlayer ePlayer = (EntityPlayer) vePlayer.getEntityPlayer();
        final DataWatcher watcher = ePlayer.getDataWatcher();
        // https://wiki.vg/Entity_metadata#Player
        // index (minecraft 1.15) -> 16
        watcher.set(DataWatcherRegistry.a.a(16), bitmask);
        PacketUtils.sendPackets(
                player,
                new PacketPlayOutNamedEntitySpawn(ePlayer),
                new PacketPlayOutEntityMetadata(
                        ePlayer.getId(),
                        watcher,
                        true
                )
        );
    }

}

/*
 * PlayerNPCPacket.java
 * 
 * Copyright (c) 2019 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package jp.gr.java_conf.zakuramomiji.renewatelier.version.packet;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.nms.VEntityPlayer;
import net.minecraft.server.v1_13_R2.ChatMessage;
import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public class FakePlayerPacket {

    public static VEntityPlayer createEntityPlayer(final World world, final Location location, final UUID uuid, final String name) {
        final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        final WorldServer nmsworld = ((CraftWorld) location.getWorld()).getHandle();
        final GameProfile profile = new GameProfile(uuid, name);
        // テクスチャが変わらない
//        final String value = "eyJ0aW1lc3RhbXAiOjE1NDg5MDEzMzg2NzYsInByb2ZpbGVJZCI6Ijg0MDEzYmIzYmJmMjQ2YzU5ZDFmN2ZkMWE3ZmQ0OGU5IiwicHJvZmlsZU5hbWUiOiJfX19Eb2xhIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iMTFhMDk2NWY1N2FhNTkzODEzY2MwOWM4YjVjYzYyNjhmMThhNmIzYjcyNTg3ZTY3MjZlODIwOWM1OTk4MDE4IiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=";
//        final String signature = "pEgASFAzeXFMzQAIoyM2/AeHaMUwCOzWKcRkx+kH8ckh5Q9QKkR7qdh/5UotPt02k6JSr6OancQhA7XjtWQWvdlyrwGVJQHYBWNEUSrQ+kx3lkRLbCcoYZkOwsVPfIocbGUZoDaKlmOtXOEpn6/to81teThJKgmlnspTePbQzWZvzSvO+60SjV5toTlUkuGX+whYMT99m9yaXh+HqnxTovdHp18VRBGCkGqetmEWxPn9WdUSGbR3VBT3Ws6YVJXv0lDo7ZoyIVFU7ejmhcjQ4SyNv8sHkPTmfY564zkxtr/Mp6yWR0PeVC2bHcFosXACIrJsIC+rGmbgEzF2bo5cFGJj77KH0t5lsK+xim+yPqVpXm1L66CZjrjnASbRBZjuqdcCoQAM6MeRT6Blz02wB2Lv87S4vIA7lmjZc8/RNdsIZUMwPU0JU89nbssq4kVo1f9KgXG7e4hpFUAgBNNCIyaXhmaDtB+w5diq0I/DGzei43WBOTJ8lmSTghDfo4zOIh/0DSJlSJ8un3EsHpRUhWiQlnbhCrPxeBLNGuOe2TnO/93i+f3kuQ6oHrvyskEuZHuw4hpdXM91PX+Hj6bESHX8vtJ0omwRZJGE2yCQDfWgMryQQ7RZGZLkXd6BBZFp+b5MzZO9mq2WzYcCEIaiPHdf5DFZHKvkuA7QyzwRga8=";
//        profile.getProperties().put("textures", new Property(value, signature));

        final EntityPlayer entityPlayer = new EntityPlayer(server, nmsworld, profile, new PlayerInteractManager(nmsworld));
        entityPlayer.listName = new ChatMessage("");
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return new VEntityPlayer(entityPlayer, entityPlayer.getId(), uuid, name, location);
    }

    public static void sendPlayer(final Player player, final List<VEntityPlayer> players, final boolean remove) {
        final List<EntityPlayer> eps = new ArrayList<>();
        players.forEach((veps) -> {
            eps.add((EntityPlayer) veps.getEntityPlayer());
        });
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        sendPackets(
                playerConnection,
                new PacketPlayOutPlayerInfo(
                        remove
                                ? PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER
                                : PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                        eps
                )
        );
    }

    public static void sendSkin(final Player player, final VEntityPlayer veplayer, final byte bitmask) {
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        final EntityPlayer eplayer = (EntityPlayer) veplayer.getEntityPlayer();
        final DataWatcher watcher = eplayer.getDataWatcher();
        watcher.set(DataWatcherRegistry.a.a(13), bitmask);
        sendPackets(
                playerConnection,
                new PacketPlayOutEntityMetadata(
                        eplayer.getId(),
                        watcher,
                        true
                ),
                new PacketPlayOutNamedEntitySpawn(eplayer)
        );
    }

    private static void sendPackets(PlayerConnection playerConnection, Packet<?>... packets) {
        for (final Packet<?> packet : packets) {
            playerConnection.sendPacket(packet);
        }
    }

}

/*
 * NPCManager.java
 * 
 * Copyright (c) 2018 firiz.
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
package jp.gr.java_conf.zakuramomiji.renewatelier.npc;

import com.comphenix.protocol.ProtocolManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.script.ScriptException;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.loop.LoopManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.packet.PacketUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.conversation.NPCConversation;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.execution.ScriptManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SQLManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import net.minecraft.server.v1_13_R2.ChatMessage;
import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 *
 * @author firiz
 */
public enum NPCManager {
    INSTANCE;
    /*
        --- sublayer all bitmask ---
    
        final byte[] sublayer_bitmasks = new byte[]{
                0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x30, 0x40
        };
        byte all_flag = 0;
        for (final byte bitmask : sublayer_bitmasks) {
                all_flag |= bitmask;
        }
     */
    public static final String CHECK = "§n§b§c";
    private final Map<UUID, NPCConversation> scriptPlayers = new HashMap<>();
    private final List<PlayerNPC> npcs = new ArrayList<>();
    private final Map<Location, String> playerNpcs = new HashMap<>();

    private void sendPackets(PlayerConnection playerConnection, Packet<?>... packets) {
        for (final Packet<?> packet : packets) {
            playerConnection.sendPacket(packet);
        }
    }

    public void packet(final Player player) {
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        final List<EntityPlayer> eps = new ArrayList<>();
        for (final PlayerNPC npc : npcs) {
            if (player.getWorld().equals(npc.getWorld())) {
                eps.add(npc.getEntity());
            }
        }
        sendPackets(
                playerConnection,
                new PacketPlayOutPlayerInfo(
                        EnumPlayerInfoAction.ADD_PLAYER,
                        eps
                )
        );
        for (final EntityPlayer eplayer : eps) {
            final DataWatcher watcher = eplayer.getDataWatcher();
            watcher.set(DataWatcherRegistry.a.a(13), (byte) 127); // 127 = all flag value
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
        Bukkit.getScheduler().runTaskLater(
                AtelierPlugin.getPlugin(),
                () -> {
                    sendPackets(
                            playerConnection,
                            new PacketPlayOutPlayerInfo(
                                    EnumPlayerInfoAction.REMOVE_PLAYER,
                                    eps
                            )
                    );
                },
                200 // 10 sec
        );
    }

    public void setup(final ProtocolManager protocolManager) {
        // load sql npcdata
        final List<List<Object>> resultObjects = SQLManager.INSTANCE.select("npcs", new String[]{
            "id", // 0
            "name", // 1
            "script", // 2
            "entityType", // 3
            "world", // 4
            "x", // 5
            "y", // 6
            "z", // 7
            "skin_uuid" // 8
        }, null);
        for (final List<Object> objs : resultObjects) {
            final EntityType type = EntityType.valueOf((String) objs.get(3));
            final Object skin_uuid = objs.get(8);
            if (type == EntityType.PLAYER && skin_uuid != null) {
                createNPCPlayer(
                        new Location(
                                Bukkit.getWorld((String) objs.get(4)),
                                (double) objs.get(5),
                                (double) objs.get(6),
                                (double) objs.get(7)
                        ),
                        (String) objs.get(1), // name
                        UUID.fromString((String) skin_uuid),
                        (String) objs.get(2), // script
                        false
                );
            } else if (type.isAlive()) {
                final World world = Bukkit.getWorld((String) objs.get(4));
                final Location loc = new Location(
                        world,
                        (double) objs.get(5),
                        (double) objs.get(6),
                        (double) objs.get(7)
                );
                world.loadChunk(world.getChunkAt(loc));
                createNPC(
                        loc,
                        type,
                        (String) objs.get(1), // name
                        (String) objs.get(2), // script
                        false
                );
            }
        }

        // addLoopEffect 0.5sec
        LoopManager.INSTANCE.addLoopEffectHalfSec(() -> {
            Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
                for (final PlayerNPC npc : npcs) {
                    final Location loc = npc.getLocation().clone();
                    if (Chore.distanceSq(loc, player.getLocation(), 300, 5)) {
                        final Vector target = player.getLocation().toVector();
                        loc.setDirection(target.subtract(loc.toVector()));
                        PacketUtils.sendPacket(player, PacketUtils.getLookPacket(
                                npc.getEntity().getId(),
                                loc.getPitch(),
                                loc.getYaw(),
                                true
                        ));
                        PacketUtils.sendPacket(player, PacketUtils.getHeadRotationPacket(
                                npc.getEntity().getId(),
                                loc.getYaw()
                        ));
                    }
                }

                player.getNearbyEntities(10, 5, 10).stream().filter((entity) -> (entity instanceof LivingEntity)).forEachOrdered((entity) -> {
                    if (entity instanceof LivingEntity) {
                        final LivingEntity lentity = (LivingEntity) entity;
                        final EntityEquipment equipment = lentity.getEquipment();
                        if (equipment != null && equipment.getBoots() != null && equipment.getBoots().hasItemMeta()) {
                            final String name = equipment.getBoots().getItemMeta().getDisplayName();
                            if (name != null && name.contains("§k§k§k")) {
                                final String[] datas = name.split("§k§k§k");
                                if (datas[0].equals(NPCManager.CHECK)) {
                                    final Location loc = entity.getLocation();
                                    final Vector target = player.getLocation().toVector();
                                    loc.setDirection(target.subtract(loc.toVector()));
                                    PacketUtils.sendPacket(player, PacketUtils.getLookPacket(
                                            entity.getEntityId(),
                                            loc.getPitch(),
                                            loc.getYaw(),
                                            entity.isOnGround()
                                    ));
                                    PacketUtils.sendPacket(player, PacketUtils.getHeadRotationPacket(
                                            entity.getEntityId(),
                                            loc.getYaw()
                                    ));
                                }
                            }
                        }
                    }
                });
            });
        });
    }

    public void createNPC(final Location location, final EntityType type, final String name, final String script) {
        createNPC(location, type, name, script, false);
    }

    public void createNPC(final Location location, final EntityType type, final String name, final String script, final boolean save) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
            final LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
            entity.setCustomName(name);
            entity.setCustomNameVisible(true);
            entity.setRemoveWhenFarAway(false);
            entity.setAI(false);

            final ItemStack item = new ItemStack(Material.STONE_BUTTON);
            final ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(NPCManager.CHECK.concat("§k§k§k").concat(Chore.createStridColor(script)));
            item.setItemMeta(meta);
            entity.getEquipment().setBoots(item);
            entity.getEquipment().setBootsDropChance(0);
        });

        if (save) {
            SQLManager.INSTANCE.insert("npcs", new String[]{
                "name", "script", "entityType",
                "world",
                "x", "y", "z"
            }, new Object[]{
                name, script, type.toString(),
                location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ()
            });
        }
    }

    public void createNPCPlayer(final Location location, final String name, final UUID uuid, final String script) {
        createNPCPlayer(location, name, uuid, script, false);
    }

    public void createNPCPlayer(final Location location, final String name, final UUID uuid, final String script, final boolean save) {
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
        npcs.add(new PlayerNPC(entityPlayer));
        playerNpcs.put(new Location(
                location.getWorld(),
                location.getX(),
                location.getY(),
                location.getZ()
        ), script);

        Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), () -> {
            for (int i = 0; i < 4; i++) {
                final Location cloc = location.clone();
                cloc.setX(location.getX() + (i == 0 ? 0.2 : i == 1 ? -0.2 : 0));
                cloc.setZ(location.getZ() + (i == 2 ? 0.35 : i == 3 ? -0.35 : 0));

                final ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(cloc, EntityType.ARMOR_STAND);
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setCustomName("npc," + script + "," + location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ());
                stand.setCustomNameVisible(false);

            }
        });

        if (save) {
            SQLManager.INSTANCE.insert("npcs", new String[]{
                "name", "script", "entityType",
                "world",
                "x", "y", "z",
                "skin_uuid"
            }, new Object[]{
                name, script, EntityType.PLAYER.toString(),
                location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ(),
                uuid.toString()
            });
        }
    }

    public boolean start(final Player player, final LivingEntity entity, final boolean shift) {
        final String customName = entity.getCustomName();
        if (customName != null && customName.startsWith("npc,") && entity instanceof ArmorStand && !((ArmorStand) entity).isVisible()) {
            final String[] datas = customName.split(",");
            final UUID uuid = player.getUniqueId();
            final Location loc = new Location(
                    Bukkit.getWorld(datas[2]),
                    Double.parseDouble(datas[3]),
                    Double.parseDouble(datas[4]),
                    Double.parseDouble(datas[5])
            );
            EntityPlayer entityPlayer = null;
            for (final PlayerNPC npc : npcs) {
                final Location nvpl = npc.getLocation().clone();
                nvpl.setPitch(0);
                nvpl.setYaw(0);
                if (nvpl.equals(loc)) {
                    entityPlayer = npc.getEntity();
                    break;
                }
            }
            if (entityPlayer != null) {
                if (scriptPlayers.containsKey(uuid)
                        && scriptPlayers.get(uuid).getPlayerNPC() != null
                        && scriptPlayers.get(uuid).getPlayerNPC().equals(entityPlayer)) {
                    final NPCConversation npcc = scriptPlayers.get(uuid);
                    try {
                        npcc.getIv().invokeFunction("action", shift);
                    } catch (ScriptException ex) {
                        Chore.log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                    }
                } else {
                    final String script = "npc/".concat(datas[1]).concat(".js");
                    final NPCConversation conversation = new NPCConversation(entityPlayer, script, player);
                    ScriptManager.INSTANCE.start(script, player, conversation, "action", shift);
                    scriptPlayers.put(uuid, conversation);
                }
                return true;
            }
        } else if (entity.getEquipment() != null && entity.getEquipment().getBoots() != null) {
            final ItemStack boots = entity.getEquipment().getBoots();
            if (boots.hasItemMeta()) {
                final String name = boots.getItemMeta().getDisplayName();
                if (name != null) {
                    if (name.contains("§k§k§k")) {
                        final String[] datas = name.split("§k§k§k");
                        if (datas[0].equals(CHECK)) {
                            final UUID uuid = player.getUniqueId();
                            if (scriptPlayers.containsKey(uuid)
                                    && scriptPlayers.get(uuid).getNPC() != null
                                    && scriptPlayers.get(uuid).getNPC().equals(entity)) {
                                final NPCConversation npcc = scriptPlayers.get(uuid);
                                try {
                                    npcc.getIv().invokeFunction("action", shift);
                                } catch (ScriptException ex) {
                                    Chore.log(Level.SEVERE, null, ex);
                                } catch (NoSuchMethodException ex) {
                                }
                            } else {
                                final String script = "npc/".concat(Chore.getStridColor(datas[1]));
                                final NPCConversation conversation = new NPCConversation(entity, script, player);
                                ScriptManager.INSTANCE.start(script, player, conversation, "action", shift);
                                scriptPlayers.put(uuid, conversation);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void dispose(final UUID uuid) {
        scriptPlayers.remove(uuid);
    }

    public NPCConversation getNPCConversation(final UUID uuid) {
        return scriptPlayers.get(uuid);
    }

}

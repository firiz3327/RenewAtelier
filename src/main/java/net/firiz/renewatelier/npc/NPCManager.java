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
package net.firiz.renewatelier.npc;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.loop.LoopManager;
import net.firiz.renewatelier.script.conversation.NPCConversation;
import net.firiz.renewatelier.script.execution.ScriptManager;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.nms.VEntityPlayer;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakePlayerPacket;
import net.firiz.renewatelier.version.packet.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.util.*;

/**
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
    private final List<LivingEntity> npcs = new ArrayList<>();
    private final List<VEntityPlayer> playerNpcs = new ArrayList<>();
    private final Map<Location, String> playerNpcLocs = new HashMap<>();

    public void packet(@NotNull final Player player) {
        final List<VEntityPlayer> eps = new ArrayList<>();
        playerNpcs.stream().filter(
                (npc) -> (player.getWorld().equals(npc.getWorld()))
        ).forEachOrdered(eps::add);
        if (!eps.isEmpty()) {
            FakePlayerPacket.sendPlayer(player, eps, false);
            eps.forEach((eplayer) -> {
                FakePlayerPacket.sendSkin(player, eplayer, (byte) 127); // 127 = all flag value
            });
            Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(),
                    () -> FakePlayerPacket.sendPlayer(player, eps, true),
                    200 // 10 sec
            );
        }
    }

    public void stop() {
        // despawn npcs
        npcs.forEach(Entity::remove);

        // despawn player npcs
        Bukkit.getWorlds().forEach((world) -> world.getPlayers().forEach((player) -> {
            npcs.stream()
                    .filter(npc -> player.getWorld().equals(npc.getWorld()))
                    .forEach(npc -> PacketUtils.sendPacket(
                            player,
                            EntityPacket.getDespawnPacket(npc.getEntityId())
                    ));

            final List<VEntityPlayer> eps = new ArrayList<>();
            playerNpcs.stream().filter(
                    (npc) -> (player.getWorld().equals(npc.getWorld()))
            ).forEachOrdered(eps::add);
            FakePlayerPacket.sendLogout(player, eps);
        }));
    }

    public void setup() {
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

        // send player npcs
        Bukkit.getWorlds().forEach((world) -> world.getPlayers().forEach(this::packet));

        // addLoopEffect 0.5sec
        LoopManager.INSTANCE.addLoopEffectHalfSec(() -> Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
            for (final VEntityPlayer npc : playerNpcs) {
                final Location loc = npc.getLocation().clone();
                if (Chore.distanceSq(loc, player.getLocation(), 300, 5)) {
                    final Vector target = player.getLocation().toVector();
                    loc.setDirection(target.subtract(loc.toVector()));
                    PacketUtils.sendPacket(player, EntityPacket.getLookPacket(
                            npc.getId(),
                            loc.getPitch(),
                            loc.getYaw(),
                            true
                    ));
                    PacketUtils.sendPacket(player, EntityPacket.getHeadRotationPacket(
                            npc.getId(),
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
                                PacketUtils.sendPacket(player, EntityPacket.getLookPacket(
                                        entity.getEntityId(),
                                        loc.getPitch(),
                                        loc.getYaw(),
                                        entity.isOnGround()
                                ));
                                PacketUtils.sendPacket(player, EntityPacket.getHeadRotationPacket(
                                        entity.getEntityId(),
                                        loc.getYaw()
                                ));
                            }
                        }
                    }
                }
            });
        }));
    }

    public void removeNPC(@NotNull final Location location, @NotNull final EntityType type, @NotNull final String name, @NotNull final String script) {
        for (final LivingEntity entity : new ArrayList<>(npcs)) {
            if (entity.getLocation().equals(location)
                    && entity.getType() == type
                    && entity.getName().equals(name)) {
                if (entity.getEquipment() != null && entity.getEquipment().getBoots() != null) {
                    final ItemStack boots = entity.getEquipment().getBoots();
                    if (boots.hasItemMeta()) {
                        final String item_name = boots.getItemMeta().getDisplayName();
                        if (item_name != null) {
                            if (item_name.contains("§k§k§k")) {
                                final String[] datas = item_name.split("§k§k§k");
                                if (datas[0].equals(CHECK) && datas[1].equals(script)) {
                                    npcs.remove(entity);
                                    entity.remove();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void createNPC(@NotNull final Location location, @NotNull final EntityType type, @NotNull final String name, @NotNull final String script) {
        createNPC(location, type, name, script, false);
    }

    public void createNPC(@NotNull final Location location, @NotNull final EntityType type, @NotNull final String name, @NotNull final String script, final boolean save) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
            final LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
            entity.setCustomName(name);
            entity.setCustomNameVisible(true);
            entity.setRemoveWhenFarAway(false);
            entity.setAI(false);
            npcs.add(entity);

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

    public void removeNPCPlayer(@NotNull final Location location, @NotNull final String name, @NotNull final UUID uuid, @NotNull final String script) {
        for (final VEntityPlayer entityPlayer : new ArrayList<>(playerNpcs)) {
            if (entityPlayer.getName().equals(name)
                    && entityPlayer.getLocation().equals(location)
                    && entityPlayer.getUniqueId().equals(uuid)) {
                playerNpcs.remove(entityPlayer);

                // remove armorstand
                for (int i = 0; i < 4; i++) {
                    final Location cloc = location.clone();
                    cloc.setX(location.getX() + (i == 0 ? 0.2 : i == 1 ? -0.2 : 0));
                    cloc.setZ(location.getZ() + (i == 2 ? 0.35 : i == 3 ? -0.35 : 0));
                    int j = 1;
                    for (final Entity entity : cloc.getWorld().getEntities()) {
                        if (cloc.equals(entity.getLocation())) {
                            entity.remove();
                            if (j >= 4) {
                                break;
                            }
                            j++;
                        }
                    }
                }
                break;
            }
        }
    }

    public void createNPCPlayer(@NotNull final Location location, @NotNull final String name, @NotNull final UUID uuid, @NotNull final String script) {
        createNPCPlayer(location, name, uuid, script, false);
    }

    public void createNPCPlayer(@NotNull final Location location, @NotNull final String name, @NotNull final UUID uuid, @NotNull final String script, final boolean save) {
        final VEntityPlayer entityPlayer = FakePlayerPacket.createEntityPlayer(location.getWorld(), location, uuid, name);
        entityPlayer.setListName("");
        playerNpcs.add(entityPlayer);
        playerNpcLocs.put(new Location(
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

    public boolean start(@NotNull final Player player, final LivingEntity entity, final boolean shift) {
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
            VEntityPlayer entityPlayer = null;
            for (final VEntityPlayer npc : playerNpcs) {
                final Location nvpl = npc.getLocation().clone();
                if (nvpl.getWorld().equals(loc.getWorld())
                        && nvpl.getX() == loc.getX()
                        && nvpl.getY() == loc.getY()
                        && nvpl.getZ() == loc.getZ()) {
                    entityPlayer = npc;
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
                        Chore.logWarning(ex);
                    } catch (NoSuchMethodException ignored) {
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
                                    Chore.logWarning(ex);
                                } catch (NoSuchMethodException ignored) {
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

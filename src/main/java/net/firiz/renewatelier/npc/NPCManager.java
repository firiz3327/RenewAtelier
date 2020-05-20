package net.firiz.renewatelier.npc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.md_5.bungee.api.ChatColor;
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
    /**
     * --- sublayer all bitmask ---
     * <p>
     * final byte[] sublayer_bitmasks = new byte[]{
     * 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x30, 0x40
     * };
     * byte all_flag = 0;
     * for (final byte bitmask : sublayer_bitmasks) {
     * all_flag |= bitmask;
     * }
     **/
    public static final String CHECK = "§n§b§c";
    private static final String STRING_SCRIPT = "script";
    private static final String STRING_ENTITY_TYPE = "entityType";
    private static final String STRING_WORLD = "world";
    private static final String STRING_ACTION = "action";
    private static final String STRING_KEY = "§k§k§k";
    private final Map<UUID, NPCConversation> scriptPlayers = new Object2ObjectOpenHashMap<>();
    private final List<LivingEntity> npcs = new ObjectArrayList<>();
    private final List<VEntityPlayer> playerNpcs = new ObjectArrayList<>();
    private final Map<Location, String> playerNpcLocs = new Object2ObjectOpenHashMap<>();

    public void packet(@NotNull final Player player) {
        final List<VEntityPlayer> eps = new ObjectArrayList<>();
        playerNpcs.stream().filter(
                npc -> (player.getWorld().equals(npc.getWorld()))
        ).forEachOrdered(eps::add);
        if (!eps.isEmpty()) {
            FakePlayerPacket.sendPlayer(player, eps, false);
            eps.forEach(ePlayer ->
                    FakePlayerPacket.sendSkin(player, ePlayer, (byte) 127) // 127 = all flag value
            );
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
        Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(player -> {
            npcs.stream()
                    .filter(npc -> player.getWorld().equals(npc.getWorld()))
                    .forEach(npc -> PacketUtils.sendPacket(
                            player,
                            EntityPacket.getDespawnPacket(npc.getEntityId())
                    ));

            final List<VEntityPlayer> eps = new ObjectArrayList<>();
            playerNpcs.stream().filter(npc -> (player.getWorld().equals(npc.getWorld()))).forEachOrdered(eps::add);
            FakePlayerPacket.sendLogout(player, eps);
        }));
    }

    public void setup() {
        // load sql npcdata
        final List<List<Object>> resultObjects = SQLManager.INSTANCE.select("npcs", new String[]{
                "id", // 0
                "name", // 1
                STRING_SCRIPT, // 2
                STRING_ENTITY_TYPE, // 3
                STRING_WORLD, // 4
                "x", // 5
                "y", // 6
                "z", // 7
                "skinUUID", // 8
                "villagerType", // 9
                "profession" // 10
        }, null);
        for (final List<Object> objs : resultObjects) {
            final EntityType type = EntityType.valueOf((String) objs.get(3));
            final Object skinUUID = objs.get(8);
            if (type == EntityType.PLAYER && skinUUID != null) {
                createNPCPlayer(
                        new Location(
                                Bukkit.getWorld((String) objs.get(4)),
                                (double) objs.get(5),
                                (double) objs.get(6),
                                (double) objs.get(7)
                        ),
                        (String) objs.get(1), // name
                        UUID.fromString((String) skinUUID),
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
                assert world != null;
                world.loadChunk(world.getChunkAt(loc));
                createNPC(
                        loc,
                        type,
                        (String) objs.get(8), // villager_type
                        (String) objs.get(9), // profession
                        (String) objs.get(1), // name
                        (String) objs.get(2), // script
                        false
                );
            }
        }

        // send player npcs
        Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(this::packet));

        LoopManager.INSTANCE.addSec(() -> Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            for (final VEntityPlayer npc : playerNpcs) {
                final Location loc = npc.getLocation().clone();
                if (Chore.distanceSq(loc, player.getLocation(), 15, 5)) {
                    final Location eyeLoc = player.getLocation();
                    if (player.isSneaking()) {
                        eyeLoc.setY(eyeLoc.getY() - 0.5);
                    }
                    final Vector target = eyeLoc.toVector();
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

            player.getNearbyEntities(3, 5, 3).stream().filter(entity -> (entity instanceof LivingEntity)).forEachOrdered(entity -> {
                final LivingEntity lEntity = (LivingEntity) entity;
                final EntityEquipment equipment = lEntity.getEquipment();
                if (equipment != null && equipment.getBoots() != null && equipment.getBoots().hasItemMeta()) {
                    final String name = Objects.requireNonNull(equipment.getBoots().getItemMeta()).getDisplayName();
                    if (name.contains(STRING_KEY)) {
                        final String[] datas = name.split(STRING_KEY);
                        if (datas[0].equals(NPCManager.CHECK)) {
                            final Location loc = lEntity.getLocation();
                            final Location eyeLoc = player.getLocation();
                            if (player.isSneaking()) {
                                eyeLoc.setY(eyeLoc.getY() - 0.5);
                            }
                            final Vector target = eyeLoc.toVector();
                            loc.setDirection(target.subtract(loc.toVector()));
                            PacketUtils.sendPacket(player, EntityPacket.getLookPacket(
                                    lEntity.getEntityId(),
                                    loc.getPitch(),
                                    loc.getYaw(),
                                    lEntity.isOnGround()
                            ));
                            PacketUtils.sendPacket(player, EntityPacket.getHeadRotationPacket(
                                    lEntity.getEntityId(),
                                    loc.getYaw()
                            ));
                        }
                    }
                }
            });
        }));
    }

    public void removeNPC(@NotNull final Location location, @NotNull final EntityType type, @NotNull final String name, @NotNull final String script) {
        for (final LivingEntity entity : new ObjectArrayList<>(npcs)) {
            if (entity.getLocation().equals(location)
                    && entity.getType() == type
                    && entity.getName().equals(name)
                    && entity.getEquipment() != null
                    && entity.getEquipment().getBoots() != null) {
                final ItemStack boots = entity.getEquipment().getBoots();
                if (boots.hasItemMeta()) {
                    final String itemName = Objects.requireNonNull(boots.getItemMeta()).getDisplayName();
                    if (itemName.contains(STRING_KEY)) {
                        final String[] datas = itemName.split(STRING_KEY);
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

    public void createNPC(@NotNull final Location location, @NotNull final EntityType type, @NotNull final String name, @NotNull final String script) {
        createNPC(location, type, name, script, false);
    }

    public void createNPC(@NotNull final Location location, @NotNull final EntityType type, @NotNull final String name, @NotNull final String script, final boolean save) {
        createNPC(location, type, null, null, name, script, save);
    }

    public void createNPC(@NotNull final Location location, @NotNull final EntityType type, final String villagerType, final String profession, @NotNull final String name, @NotNull final String script, final boolean save) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
            final LivingEntity entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, type);
            if (entity instanceof Villager) {
                final Villager villager = (Villager) entity;
                if (villagerType != null) {
                    villager.setVillagerType(Villager.Type.valueOf(villagerType.toUpperCase()));
                }
                if (profession != null) {
                    villager.setProfession(Villager.Profession.valueOf(profession.toUpperCase()));
                }
            }
            entity.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
            entity.setCustomNameVisible(false);
            entity.setRemoveWhenFarAway(false);
            entity.setAI(false);
            entity.setCanPickupItems(false);
            entity.setInvulnerable(true);
            entity.setCollidable(false);
            entity.setGravity(false);
            npcs.add(entity);

            final ItemStack item = new ItemStack(Material.STONE_BUTTON);
            final ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(NPCManager.CHECK.concat(STRING_KEY).concat(Chore.createStridColor(script)));
            item.setItemMeta(meta);
            Objects.requireNonNull(entity.getEquipment()).setBoots(item);
            entity.getEquipment().setBootsDropChance(0);
        });

        if (save) {
            SQLManager.INSTANCE.insert("npcs", new String[]{
                    "name", STRING_SCRIPT, STRING_ENTITY_TYPE,
                    STRING_WORLD,
                    "x", "y", "z"
            }, new Object[]{
                    name, script, type.toString(),
                    Objects.requireNonNull(location.getWorld()).getName(),
                    location.getX(), location.getY(), location.getZ()
            });
        }
    }

    public void removeNPCPlayer(@NotNull final Location location, @NotNull final String name, @NotNull final UUID uuid, @NotNull final String script) {
        for (final VEntityPlayer entityPlayer : new ObjectArrayList<>(playerNpcs)) {
            if (entityPlayer.getName().equals(name)
                    && entityPlayer.getLocation().equals(location)
                    && entityPlayer.getUniqueId().equals(uuid)) {
                playerNpcs.remove(entityPlayer);

                // remove armorstand
                for (int i = 0; i < 4; i++) {
                    final Location cloc = getStandLocation(location, i);
                    int j = 1;
                    for (final Entity entity : Objects.requireNonNull(cloc.getWorld()).getEntities()) {
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
        final VEntityPlayer entityPlayer = FakePlayerPacket.createEntityPlayer(
                location.getWorld(),
                location,
                uuid,
                ChatColor.translateAlternateColorCodes('&', name)
        );
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
                final Location cloc = getStandLocation(location, i);
                final ArmorStand stand = (ArmorStand) Objects.requireNonNull(location.getWorld()).spawnEntity(cloc, EntityType.ARMOR_STAND);
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setCustomName("npc," + script + "," + location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ());
                stand.setCustomNameVisible(false);
            }
        });

        if (save) {
            SQLManager.INSTANCE.insert("npcs", new String[]{
                    "name", STRING_SCRIPT, STRING_ENTITY_TYPE,
                    STRING_WORLD,
                    "x", "y", "z",
                    "skinUUID"
            }, new Object[]{
                    name, script, EntityType.PLAYER.toString(),
                    Objects.requireNonNull(location.getWorld()).getName(),
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
                if (Objects.equals(nvpl.getWorld(), loc.getWorld())
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
                        && Objects.equals(scriptPlayers.get(uuid).getPlayerNPC(), entityPlayer)) {
                    final NPCConversation npcc = scriptPlayers.get(uuid);
                    try {
                        npcc.getIv().invokeFunction(STRING_ACTION, shift);
                    } catch (ScriptException ex) {
                        Chore.logWarning(ex);
                    } catch (NoSuchMethodException ignored) {
                    }
                } else {
                    final String script = "npc/".concat(datas[1].replace(ChatColor.BLACK.toString(), ""));
                    final NPCConversation conversation = new NPCConversation(entityPlayer, script, player);
                    ScriptManager.INSTANCE.start(script, player, conversation, STRING_ACTION, shift);
                    scriptPlayers.put(uuid, conversation);
                }
                return true;
            }
        } else if (entity.getEquipment() != null && entity.getEquipment().getBoots() != null) {
            final ItemStack boots = entity.getEquipment().getBoots();
            if (boots.hasItemMeta()) {
                final String name = Objects.requireNonNull(boots.getItemMeta()).getDisplayName();
                if (name.contains(STRING_KEY)) {
                    final String[] datas = name.split(STRING_KEY);
                    if (datas[0].equals(CHECK)) {
                        final UUID uuid = player.getUniqueId();
                        if (scriptPlayers.containsKey(uuid)
                                && scriptPlayers.get(uuid).getIv() != null
                                && scriptPlayers.get(uuid).getNPC() != null
                                && Objects.equals(scriptPlayers.get(uuid).getNPC(), entity)) {
                            final NPCConversation npcc = scriptPlayers.get(uuid);
                            try {
                                npcc.getIv().invokeFunction(STRING_ACTION, shift);
                            } catch (ScriptException ex) {
                                Chore.logWarning(ex);
                            } catch (NoSuchMethodException ignored) {
                            }
                        } else {
                            final String script = "npc/".concat(Chore.getStridColor(datas[1]));
                            final NPCConversation conversation = new NPCConversation(entity, script, player);
                            ScriptManager.INSTANCE.start(script, player, conversation, STRING_ACTION, shift);
                            scriptPlayers.put(uuid, conversation);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Location getStandLocation(Location location, int i) {
        final Location cloc = location.clone();
        cloc.setX(location.getX() + (i == 0 ? 0.2 : i == 1 ? -0.2 : 0));
        cloc.setZ(location.getZ() + (i == 2 ? 0.35 : i == 3 ? -0.35 : 0));
        return cloc;
    }

    public void dispose(final UUID uuid) {
        scriptPlayers.remove(uuid);
    }

    public NPCConversation getNPCConversation(final UUID uuid) {
        return scriptPlayers.get(uuid);
    }

}

package net.firiz.renewatelier.npc;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.loop.TickRunnable;
import net.firiz.ateliercommonapi.nms.entity.NMSLivingEntity;
import net.firiz.ateliercommonapi.nms.entity.player.NMSPlayer;
import net.firiz.ateliercommonapi.nms.packet.EntityPacket;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.ateliercommonapi.utils.pair.longs.ObjectLongNonNullMutablePair;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.server.script.conversation.NPCConversation;
import net.firiz.renewatelier.server.script.execution.ScriptManager;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.java.CObjects;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.*;

public enum NPCManager {
    INSTANCE;

    private final List<NPC> npcList = new ObjectArrayList<>();
    private final Map<UUID, ObjectLongNonNullMutablePair<NPCConversation>> scriptPlayers = new Object2ObjectOpenHashMap<>();

    public void load() {
        SQLManager.INSTANCE.select("npcs", new String[]{
                "id", // 0
                "name", // 1
                "script", // 2
                "entityType", // 3
                "world", // 4
                "x", // 5
                "y", // 6
                "z", // 7
                "skinUUID", // 8
                "villagerType", // 9
                "profession" // 10
        }, null).stream().map(objects ->
                new NPCObject(
                        (String) objects.get(1),
                        (String) objects.get(2),
                        EntityType.valueOf((String) objects.get(3)),
                        Objects.requireNonNull(Bukkit.getWorld((String) objects.get(4))),
                        (double) objects.get(5),
                        (double) objects.get(6),
                        (double) objects.get(7),
                        CObjects.nullIfFunction((String) objects.get(8), skin -> {
                            if (!skin.isEmpty()) {
                                return UUID.fromString(skin);
                            }
                            return null;
                        }, null),
                        (String) objects.get(9),
                        (String) objects.get(10)
                ).createNPC()
        ).forEach(npcList::add);
    }

    public TickRunnable npcLoop() {
        return () -> Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            for (final NPC npc : npcList) {
                final Location loc = npc.getLocation().clone();
                if (CommonUtils.distanceSq(loc, player.getLocation(), 150, 10)) {
                    if (!npc.hasViewer(player)) {
                        npc.addViewer(player);
                        if (npc.isPlayer()) {
                            PacketUtils.sendPacket(player, EntityPacket.playerPacket(Collections.singletonList(npc.getEntityPlayer()), false));
                            PacketUtils.sendPackets(player, EntityPacket.skinPackets(npc.getEntityPlayer(), (byte) 127)); // 127 = sublayerBitmasks
                        } else {
                            PacketUtils.sendPackets(player, EntityPacket.spawnLivingPackets(npc.getEntity()));
                        }
                    }
                    if (CommonUtils.distanceSq(loc, player.getLocation(), 15, 5)) {
                        final Location eyeLoc = player.getLocation();
                        if (player.isSneaking()) {
                            eyeLoc.setY(eyeLoc.getY() - 0.5);
                        }
                        final org.bukkit.util.Vector target = eyeLoc.toVector();
                        loc.setDirection(target.subtract(loc.toVector()));
                        PacketUtils.sendPacket(player, EntityPacket.lookPacket(
                                npc.getEntityId(),
                                loc.getPitch(),
                                loc.getYaw(),
                                true
                        ));
                        PacketUtils.sendPacket(player, EntityPacket.headRotationPacket(
                                npc.getEntityId(),
                                loc.getYaw()
                        ));
                    }
                } else if (npc.hasViewer(player)) {
                    npc.removeViewer(player);
                    if (npc.isPlayer()) {
                        PacketUtils.sendPackets(player, EntityPacket.logoutPlayerPacket(Collections.singletonList(npc.getEntityPlayer())));
                    } else {
                        PacketUtils.sendPacket(player, EntityPacket.despawnPacket(npc.getEntityId()));
                    }
                }
            }
        });
    }

    public void stop() {
        final Map<World, List<NMSPlayer>> worldFakePlayers = new Object2ObjectOpenHashMap<>();
        final Map<World, IntList> worldFakeEntities = new Object2ObjectOpenHashMap<>();
        npcList.forEach(npc -> {
            if (npc.isPlayer()) {
                final NMSPlayer entityPlayer = npc.getEntityPlayer();
                final World world = entityPlayer.world();
                if (worldFakePlayers.containsKey(world)) {
                    worldFakePlayers.get(world).add(entityPlayer);
                } else {
                    final List<NMSPlayer> list = new ObjectArrayList<>();
                    list.add(entityPlayer);
                    worldFakePlayers.put(world, list);
                }
            } else {
                final NMSLivingEntity entity = npc.getEntity();
                final World world = entity.world();
                if (worldFakeEntities.containsKey(world)) {
                    worldFakeEntities.get(world).add(entity.id());
                } else {
                    final IntList list = new IntArrayList();
                    list.add(entity.id());
                    worldFakeEntities.put(world, list);
                }
            }
        });
        Bukkit.getOnlinePlayers().forEach(player -> {
            final List<NMSPlayer> fakePlayers = worldFakePlayers.get(player.getWorld());
            if (fakePlayers != null) {
                PacketUtils.sendPackets(player, EntityPacket.logoutPlayerPacket(fakePlayers));
            }
            final IntList fakeEntities = worldFakeEntities.get(player.getWorld());
            if (fakeEntities != null) {
                PacketUtils.sendPackets(player, EntityPacket.despawnPackets(CollectionUtils.parseInts(fakeEntities)));
            }
        });
    }

    public boolean canAction(@NotNull Player player) {
        final UUID uuid = player.getUniqueId();
        if (scriptPlayers.containsKey(uuid)) {
            return canAction(System.currentTimeMillis(), scriptPlayers.get(uuid));
        }
        return true;
    }

    private boolean canAction(long now, ObjectLongNonNullMutablePair<NPCConversation> scriptPlayerValue) {
        return now - scriptPlayerValue.rightLong() >= 100;
    }

    public boolean action(@NotNull Player player, int entityId) {
        final boolean sneaking = player.isSneaking();
        final Optional<NPC> optionalNPC = npcList.stream().filter(npc -> entityId == npc.getEntityId()).findFirst();
        if (optionalNPC.isPresent()) {
            final UUID uuid = player.getUniqueId();
            final NPC npc = optionalNPC.get();
            final String script = npc.getNpcObject().getScript();
            final String scriptFile = "npc/".concat(script);

            boolean start = true;
            if (scriptPlayers.containsKey(uuid)) {
                final ObjectLongNonNullMutablePair<NPCConversation> scriptPlayerValue = scriptPlayers.get(uuid);
                if (scriptFile.equals(scriptPlayerValue.left().getScriptName())) {
                    start = false;
                    final long now = System.currentTimeMillis();
                    final boolean canAction = canAction(now, scriptPlayerValue);
                    if (canAction) {
                        scriptPlayerValue.right(now);
                        Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), () -> {
                            final ObjectLongNonNullMutablePair<NPCConversation> value = scriptPlayers.get(uuid);
                            if (value != null) {
                                final Invocable iv = value.left().getIv();
                                if (iv != null) {
                                    try {
                                        iv.invokeFunction("action", sneaking);
                                    } catch (ScriptException ex) {
                                        CommonUtils.logWarning(ex);
                                    } catch (NoSuchMethodException ignored) {
                                        // ignored
                                    }
                                }
                            }
                        });
                    }
                }
            }
            if (start) {
                final NPCConversation conversation = new NPCConversation(npc, scriptFile, player);
                Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), () -> ScriptManager.INSTANCE.start(scriptFile, player, conversation, "action", sneaking));
                scriptPlayers.put(uuid, new ObjectLongNonNullMutablePair<>(conversation, System.currentTimeMillis()));
            }
            return true;
        }
        return false;
    }

    public void dispose(final UUID uuid) {
        scriptPlayers.remove(uuid);
    }

    public NPCConversation getNPCConversation(final UUID uuid) {
        return scriptPlayers.get(uuid).left();
    }

}

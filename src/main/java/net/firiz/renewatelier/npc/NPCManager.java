package net.firiz.renewatelier.npc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.script.conversation.NPCConversation;
import net.firiz.renewatelier.script.execution.ScriptManager;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.chores.CObjects;
import net.firiz.renewatelier.utils.pair.NonNullPair;
import net.firiz.renewatelier.version.nms.VEntityPlayer;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakePlayerPacket;
import net.firiz.renewatelier.version.packet.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.util.*;

public enum NPCManager {
    INSTANCE;

    private final List<NPC> npcList = new ObjectArrayList<>();
    private final Map<UUID, NonNullPair<NPCConversation, Long>> scriptPlayers = new Object2ObjectOpenHashMap<>();

    public void load() {
        npcList.stream().filter(npc -> !npc.isPlayer()).forEach(npc -> npc.getEntity().remove());
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
                ).spawnEntity()
        ).forEach(npcList::add);
    }

    public Runnable lookEyeLoop() {
        return () -> Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            for (final NPC npc : npcList) {
                final Location loc = npc.getLocation();
                if (CommonUtils.distanceSq(loc, player.getLocation(), 15, 5)) {
                    final Location eyeLoc = player.getLocation();
                    if (player.isSneaking()) {
                        eyeLoc.setY(eyeLoc.getY() - 0.5);
                    }
                    final org.bukkit.util.Vector target = eyeLoc.toVector();
                    loc.setDirection(target.subtract(loc.toVector()));
                    PacketUtils.sendPacket(player, EntityPacket.getLookPacket(
                            npc.getEntityId(),
                            loc.getPitch(),
                            loc.getYaw(),
                            true
                    ));
                    PacketUtils.sendPacket(player, EntityPacket.getHeadRotationPacket(
                            npc.getEntityId(),
                            loc.getYaw()
                    ));
                }
            }
        });
    }

    public void stop() {
        final Map<World, List<VEntityPlayer>> worldFakePlayers = new Object2ObjectOpenHashMap<>();
        npcList.forEach(npc -> {
            if (npc.isPlayer()) {
                final VEntityPlayer entityPlayer = npc.getEntityPlayer();
                final World world = entityPlayer.getWorld();
                if (worldFakePlayers.containsKey(world)) {
                    worldFakePlayers.get(world).add(entityPlayer);
                } else {
                    final List<VEntityPlayer> list = new ObjectArrayList<>();
                    list.add(entityPlayer);
                    worldFakePlayers.put(world, list);
                }
            } else {
                npc.getEntity().remove();
            }
        });
        Bukkit.getOnlinePlayers().forEach(player -> {
            final List<VEntityPlayer> fakePlayers = worldFakePlayers.get(player.getWorld());
            if (fakePlayers != null) {
                FakePlayerPacket.sendLogout(player, fakePlayers);
            }
        });
    }

    public void packet(@NotNull final Player player) {
        final List<VEntityPlayer> playerNpcs = new ObjectArrayList<>();
        npcList.stream().filter(
                npc -> npc.isPlayer() && player.getWorld().equals(npc.getNpcObject().getWorld())
        ).map(NPC::getEntityPlayer).forEachOrdered(playerNpcs::add);
        if (!playerNpcs.isEmpty()) {
            FakePlayerPacket.sendPlayer(player, playerNpcs, false);
            playerNpcs.forEach(ePlayer ->
                    FakePlayerPacket.sendSkin(player, ePlayer, (byte) 127) // 127 = sublayerBitmasks
            );
        }
    }

    public boolean action(@NotNull Player player, @NotNull Entity entity) {
        if (NPCObject.hasEntity(entity)) {
            return action(player, entity.getEntityId());
        }
        return false;
    }

    public boolean canAction(@NotNull Player player) {
        final UUID uuid = player.getUniqueId();
        if (scriptPlayers.containsKey(uuid)) {
            return canAction(System.currentTimeMillis(), scriptPlayers.get(uuid));
        }
        return true;
    }

    private boolean canAction(long now, NonNullPair<NPCConversation, Long> scriptPlayerValue) {
        return now - scriptPlayerValue.getRight() >= 100;
    }

    public boolean action(@NotNull Player player, int entityId) {
        final boolean sneaking = player.isSneaking();
        final Optional<NPC> optionalNPC = npcList.stream().filter(npc -> entityId == npc.getEntityId()).findFirst();
        if (optionalNPC.isPresent()) {
            final UUID uuid = player.getUniqueId();
            final NPC npc = optionalNPC.get();
            final String script = npc.getNpcObject().getScript();

            if (scriptPlayers.containsKey(uuid)) {
                final NonNullPair<NPCConversation, Long> scriptPlayerValue = scriptPlayers.get(uuid);
                final long now = System.currentTimeMillis();
                final boolean canAction = canAction(now, scriptPlayerValue);
                if (canAction) {
                    scriptPlayerValue.setRight(now);
                    Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), () -> {
                        try {
                            Objects.requireNonNull(scriptPlayers.get(uuid).getLeft().getIv()).invokeFunction("action", sneaking);
                        } catch (ScriptException ex) {
                            CommonUtils.logWarning(ex);
                        } catch (NoSuchMethodException ignored) {
                        }
                    });
                }
            } else {
                final String scriptFile = "npc/".concat(script);
                final NPCConversation conversation = new NPCConversation(npc, scriptFile, player);
                Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), () -> ScriptManager.INSTANCE.start(scriptFile, player, conversation, "action", sneaking));
                scriptPlayers.put(uuid, new NonNullPair<>(conversation, System.currentTimeMillis()));
            }
            return true;
        }
        return false;
    }

    public void dispose(final UUID uuid) {
        scriptPlayers.remove(uuid);
    }

    public NPCConversation getNPCConversation(final UUID uuid) {
        return scriptPlayers.get(uuid).getLeft();
    }

}
